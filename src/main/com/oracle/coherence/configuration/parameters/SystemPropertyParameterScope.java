/*
 * File: SystemPropertyParameterScope.java
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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>A {@link SystemPropertyParameterScope} provide a {@link ParameterScope} based representation of
 * the current state of the defined Java System Properties.</p>
 *
 * @author Brian Oliver
 */
public class SystemPropertyParameterScope implements ParameterScope
{

    /**
     * <p>The default {@link SystemPropertyParameterScope} instance.</p>
     */
    public final static SystemPropertyParameterScope INSTANCE = new SystemPropertyParameterScope();


    /**
     * <p>Standard Constructor.</p>
     */
    public SystemPropertyParameterScope()
    {
        //deliberately empty
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        return System.getProperties().containsKey(name);
    }


    /**
     * {@inheritDoc}
     */
    public Parameter getParameter(String name)
    {
        if (isDefined(name))
        {
            return new Parameter(name, System.getProperty(name));
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Iterator<Parameter> iterator()
    {
        final Enumeration<String> propertyNames = (Enumeration<String>) System.getProperties().propertyNames();

        return new Iterator<Parameter>()
        {

            /**
             * {@inheritDoc}
             */
            public boolean hasNext()
            {
                return propertyNames.hasMoreElements();
            }


            /**
             * {@inheritDoc}
             */
            public Parameter next()
            {
                String propertyName = propertyNames.nextElement();
                return new Parameter(propertyName, System.getProperty(propertyName));
            }


            /**
             * {@inheritDoc}
             */
            public void remove()
            {
                throw new UnsupportedOperationException("Can't remove a parameter from an SystemPropertyParameterScope");
            }
        };
    }
}
