/*
 * File: NestedParameterScope.java
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
package com.oracle.coherence.configuration.parameters;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * <p>A {@link NestedParameterScope} is a {@link MutableParameterScope} that wraps/nests an inner
 * {@link ParameterScope}.  Should a {@link Parameter} not be defined by the {@link NestedParameterScope}
 * the inner/wrapped {@link ParameterScope} is consulted.  When adding {@link Parameter}s, they are only added
 * to the {@link NestedParameterScope} and not the inner/wrapped {@link ParameterScope}.</p>
 * 
 * <p>Hence this permits the scoping and hiding of {@link Parameter} definitions as inner
 * {@link ParameterScope}s are only consulted when the outer {@link NestedParameterScope} does not know
 * of a {@link Parameter}.</p>
 *
 * <p>NOTE: {@link NestedParameterScope}s may wrap/nest other {@link NestedParameterScope}s, thus
 * it's possible to create any number of layers of wrapping/nesting or {@link ParameterScope}s.</p>
 *
 * @author Brian Oliver
 */
public class NestedParameterScope implements MutableParameterScope
{

    /**
     * <p>The {@link ParameterScope} for this level.</p>
     */
    private MutableParameterScope parameterScope;

    /**
     * <p>The inner/wrapped {@link ParameterScope}.</p>
     */
    private ParameterScope innerParameterScope;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param parameterScope The {@link ParameterScope} to wrap/nest.
     */
    public NestedParameterScope(ParameterScope parameterScope)
    {
        this.parameterScope = new SimpleParameterScope();
        this.innerParameterScope = parameterScope;
    }


    /**
     * <p>Standard Constructor (nesting a {@link EmptyParameterScope}).</p>
     */
    public NestedParameterScope()
    {
        this(EmptyParameterScope.INSTANCE);
    }


    /**
     * {@inheritDoc}
     */
    public void addParameter(Parameter parameter)
    {
        parameterScope.addParameter(parameter);
    }


    /**
     * {@inheritDoc}
     */
    public Parameter getParameter(String name) throws ClassCastException
    {
        if (parameterScope.isDefined(name))
        {
            return parameterScope.getParameter(name);
        }
        else
        {
            return innerParameterScope.getParameter(name);
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        return parameterScope.isDefined(name) || innerParameterScope.isDefined(name);
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Parameter> iterator()
    {
        LinkedHashMap<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();

        //add all of the current (outer) parameters to the map
        for (Parameter parameter : parameterScope)
        {
            parameters.put(parameter.getName(), parameter);
        }

        //add all visible (not already existing in the map) parameters from the inner scope to the map 
        for (Parameter parameter : innerParameterScope)
        {
            if (!parameters.containsKey(parameter.getName()))
            {
                parameters.put(parameter.getName(), parameter);
            }
        }

        return parameters.values().iterator();
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("NestedParameterScope{parameterScope=%s, innerParameterScope=%s}",
            parameterScope, innerParameterScope);
    }
}
