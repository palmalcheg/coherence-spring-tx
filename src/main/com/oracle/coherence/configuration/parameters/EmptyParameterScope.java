/*
 * File: EmptyParameterScope.java
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

/**
 * <p>A {@link EmptyParameterScope} is an implementation of a {@link ParameterScope} that
 * contains no {@link Parameter}s.</p>
 *
 * @author Brian Oliver
 */
public class EmptyParameterScope implements ParameterScope
{

    /**
     * <p>A constant {@link EmptyParameterScope}.</p>
     */
    public static final EmptyParameterScope INSTANCE = new EmptyParameterScope();


    /**
     * <p>Standard Constructor.</p>
     */
    public EmptyParameterScope()
    {
        //deliberately empty
    }


    /**
     * {@inheritDoc}
     */
    public Parameter getParameter(String name) throws ClassCastException
    {
        //always returns null for a EmptyParameterScope
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        //always returns false for a EmptyParameterScope
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Parameter> iterator()
    {
        return new Iterator<Parameter>()
        {

            /**
             * {@inheritDoc}
             */
            public boolean hasNext()
            {
                return false;
            }


            /**
             * {@inheritDoc}
             */
            public Parameter next()
            {
                return null;
            }


            /**
             * {@inheritDoc}
             */
            public void remove()
            {
                throw new UnsupportedOperationException("Can't remove a parameter from an EmptyParameterScope");
            }
        };
    }
}
