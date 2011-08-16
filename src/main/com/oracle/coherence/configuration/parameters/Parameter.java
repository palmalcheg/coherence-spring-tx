/*
 * File: Parameter.java
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

import com.oracle.coherence.common.util.Value;

/**
 * <p>A {@link Parameter} represents an explicitly named {@link Value}.</p>
 *
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Parameter extends Value
{

    /**
     * <p>The name of the {@link Parameter}.<p>
     */
    private String name;


    /**
     * <p>Standard Constructor (for a {@link Boolean} parameter).</p>
     * 
     * @param name The name of the parameter
     * @param value The value for the parameter
     */
    public Parameter(String name,
                     boolean value)
    {
        super(value);
        this.name = name;
    }


    /**
     * <p>Standard Constructor (for an {@link Object} parameter).</p>
     * 
     * @param name The name of the parameter
     * @param value The value for the parameter
     */
    public Parameter(String name,
                     Object value)
    {
        super(value);
        this.name = name;
    }


    /**
     * <p>Standard Constructor (for a {@link String} parameter).</p>
     * 
     * @param name The name of the parameter
     * @param value The value for the parameter
     */
    public Parameter(String name,
                     String value)
    {
        super(value);
        this.name = name;
    }


    /**
     * <p>Standard Constructor (for a {@link Value} parameter).</p>
     * 
     * @param name The name of the parameter
     * @param value The value for the parameter
     */
    public Parameter(String name,
                     Value value)
    {
        super(value);
        this.name = name;
    }
    

    /**
     * <p>Returns the name of the {@link Parameter}.</p>
     * 
     * @return A {@link String} representing the name of the {@link Parameter}
     */
    public String getName()
    {
        return name;
    }
}
