/*
 * File: ParameterizedClassScheme.java
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
package com.oracle.coherence.schemes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.util.ReflectionHelper;
import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.environment.Environment;
import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.Base;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link ParameterizedClassScheme} is an implementation of a {@link ClassScheme} that is parameterized such that
 * the instances realized at runtime based on the said parameters, not a hard coded type.</p>
 *
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class ParameterizedClassScheme<T> implements ClassScheme<T>, ReflectiveScheme, ExternalizableLite,
        PortableObject
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static final Logger logger = Logger.getLogger(ParameterizedClassScheme.class.getName());

    /**
     * <p>The name of the class to create when this {@link ParameterizedClassScheme} is realized.</p>
     */
    private String className;

    /**
     * <p>The parameters for the class constructor.</p>
     * 
     * <p>NOTE: Parameters may also be instances of Schemes, in which case they may be realized.</p>
     */
    private Object[] constructorParameters;


    /**
     * <p>Standard Constructor.</p>
     * 
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public ParameterizedClassScheme()
    {
        //SKIP: deliberately empty
    }


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param className The name of the class that will be created when this {@link ParameterizedClassScheme} is realized.
     * @param constructorParameters The constructor parameters for realizing the class.
     */
    public ParameterizedClassScheme(String className,
                                    Object... constructorParameters)
    {
        this.className = className;
        this.constructorParameters = constructorParameters;
    }


    /**
     * {@inheritDoc}
     */
    public boolean realizesClassOf(Class<?> clazz)
    {
        try
        {
            //load the parameterized class, but don't initialize it
            Class<?> parameterizedClazz = Class.forName(className, false, this.getClass().getClassLoader());

            //is the class the same or a super class/interface of the parameterized Clazz
            return clazz.isAssignableFrom(parameterizedClazz);
        }
        catch (ClassNotFoundException classNotFoundException)
        {
            if (logger.isLoggable(Level.WARNING))
            {
                logger.log(Level.WARNING, String.format(
                    "Class %s not found while attempting to determine type information of a ReflectiveScheme",
                    className), classNotFoundException);
            }

            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T realize(Environment environment,
                     ClassLoader classLoader,
                     ParameterScope parameterScope)
    {
        try
        {
            //ensure that each of the ClassScheme parameters are realized
            Object[] parameters = new Object[constructorParameters.length];
            for (int i = 0; i < constructorParameters.length; i++)
            {
                if (constructorParameters[i] instanceof ClassScheme)
                {
                    parameters[i] = ((ClassScheme<T>) constructorParameters[i]).realize(environment, classLoader,
                        parameterScope);
                }
                else
                {
                    parameters[i] = constructorParameters[i];
                }
            }

            return (T) ReflectionHelper.createObject(className, parameters, classLoader);
        }
        catch (Exception exception)
        {
            throw Base.ensureRuntimeException(exception);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.className = ExternalizableHelper.readSafeUTF(in);
        int parameterCount = ExternalizableHelper.readInt(in);
        this.constructorParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++)
        {
            this.constructorParameters[i] = ExternalizableHelper.readObject(in);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeSafeUTF(out, className);
        ExternalizableHelper.writeInt(out, constructorParameters.length);
        for (int i = 0; i < constructorParameters.length; i++)
        {
            ExternalizableHelper.writeObject(out, constructorParameters[i]);
        }
    }


    public void readExternal(PofReader reader) throws IOException
    {
        this.className = reader.readString(1);
        int parameterCount = reader.readInt(2);
        this.constructorParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++)
        {
            this.constructorParameters[i] = reader.readObject(3 + i);
        }

    }


    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeString(1, className);
        writer.writeInt(2, constructorParameters.length);
        for (int i = 0; i < constructorParameters.length; i++)
        {
            writer.writeObject(3 + i, constructorParameters[i]);
        }
    }
}
