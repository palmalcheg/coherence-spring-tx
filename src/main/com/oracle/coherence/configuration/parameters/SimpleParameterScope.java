/*
 * File: SimpleParameterScope.java
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
 * <p>A {@link SimpleParameterScope} is a simple map-based implementation of a {@link MutableParameterScope}.</p>
 *
 * @author Brian Oliver
 */
public class SimpleParameterScope implements MutableParameterScope
{

    /**
     * <p>The {@link Parameter} map.</p>
     */
    private LinkedHashMap<String, Parameter> parameters;


    /**
     * <p>Standard Constructor.</p>
     */
    public SimpleParameterScope()
    {
        this.parameters = new LinkedHashMap<String, Parameter>();
    }


    /**
     * {@inheritDoc}
     */
    public void addParameter(Parameter parameter)
    {
        parameters.put(parameter.getName(), parameter);
    }


    /**
     * {@inheritDoc}
     */
    public Parameter getParameter(String name) throws ClassCastException
    {
        return parameters.get(name);
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        return parameters.containsKey(name);
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Parameter> iterator()
    {
        return parameters.values().iterator();
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("SimpleParameterScope{parameters=%s}", parameters);
    }
}
