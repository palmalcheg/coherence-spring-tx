/*
 * File: MacroParameterExpression.java
 * 
 * Copyright (c) 2010. All Rights Reserved. Oracle Corporation.
 * 
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 * 
 * This software is the confidential and proprietary information of Oracle
 * Corporation. You shall not disclose such confidential and proprietary
 * information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Oracle Corporation.
 * 
 * Oracle Corporation makes no representations or warranties about the
 * suitability of the software, either express or implied, including but not
 * limited to the implied warranties of merchantability, fitness for a
 * particular purpose, or non-infringement. Oracle Corporation shall not be
 * liable for any damages suffered by licensee as a result of using, modifying
 * or distributing this software or its derivatives.
 * 
 * This notice may not be removed or altered.
 */
package com.oracle.coherence.configuration.expressions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.oracle.coherence.common.util.Value;
import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.environment.Environment;
import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link MacroParameterExpression} represents a Coherence {@link Expression} 
 * (ie: the use of a macro parameter) within something like a Coherence Cache Configuration file.</p>
 *
 * <p>Macro Parameters are usually represented as follows.</p>
 * 
 * <code>{parameter-name default-value}</code>
 * 
 * <p>When evaluated the parameter-name is resolved by the {@link ParameterScope}.  If it's resolvable, 
 * the value of the resolved parameter is returned.  If it's not resolved the default value is returned.</p>
 *
 * @author Brian Oliver
 */
@SuppressWarnings({ "serial", "unchecked" })
public class MacroParameterExpression implements Expression, ExternalizableLite, PortableObject
{

    /**
     * <p>The name of the class representing the expected type of value that should be returned 
     * when the {@link Expression} is evaluated.</p>
     */
    private String className;

    /**
     * <p>The expression to be evaluated.</p>
     */
    private String expression;


    /**
     * <p>Standard Constructor (required for {@link ExternalizableLite} and {@link PortableObject})
     */
    public MacroParameterExpression()
    {
        //deliberately empty
    }
    
    
    /**
     * <p>Standard Constructor.</p>
     * 
     * @param type The expected type of the {@link Expression}
     * @param expression The expression
     */
    public MacroParameterExpression(Class<?> type,
                                    String expression)
    {
        this.className = type.getName();
        this.expression = expression.trim();
    }


    /**
     * {@inheritDoc}
     */
    public Object evaluate(Environment environment,
                           ClassLoader classLoader,
                           ParameterScope parameterScope)
    {
        //resolve the parameter
        Value result;
        if (expression.startsWith("{") && expression.endsWith("}"))
        {
            String parameterName;
            String parameterDefaultValue;
            int defaultValuePos = expression.indexOf(" ");
            if (defaultValuePos > 1)
            {
                parameterName = expression.substring(1, defaultValuePos).trim();
                parameterDefaultValue = expression.substring(defaultValuePos, expression.length() - 1).trim();
            }
            else
            {
                parameterName = expression.substring(1, expression.length() - 1);
                parameterDefaultValue = null;
            }

            if (parameterScope.isDefined(parameterName))
            {
                result = parameterScope.getParameter(parameterName);
            }
            else if (parameterDefaultValue != null)
            {
                result = new Value(parameterDefaultValue);
            }
            else
            {
                //the parameter is unknown
                throw new IllegalArgumentException(String.format(
                    "The specified parameter name '%s' in the macro parameter '%s' is unknown and not resolvable",
                    parameterName, expression));
            }
        }
        else
        {
            result = new Value(expression);
        }

        try
        {
            //determine the required type 
            Class<?> type = Class.forName(className, false, classLoader);

            //return the expression coerced into the required type
            return result.getValue(type);
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            //the types are incompatible
            throw new ClassCastException(String.format(
                "Could not load the specified class '%s' when evaluating the expression '%s'",
                className, expression));
        }
        catch (ClassCastException classCastException)
        {
            //the types are incompatible
            throw new ClassCastException(String.format(
                "The value '%s' of the type '%s' for the expression '%s' is incompatible with the expected type '%s'",
                result.getObject(), result.getClass().getName(), expression, className));
        }
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("MacroParameterExpression{className=%s, expression=%s}", className, expression);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.className = ExternalizableHelper.readSafeUTF(in);
        this.expression = ExternalizableHelper.readSafeUTF(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeSafeUTF(out, className);
        ExternalizableHelper.writeSafeUTF(out, expression);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.className = reader.readString(1);
        this.expression = reader.readString(2);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeString(1, className);
        writer.writeString(2, expression);
    }
}
