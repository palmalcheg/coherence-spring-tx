/*
 * File: JavaApplication.java
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
package com.oracle.coherence.system.application;

import java.util.Map;

/**
 * A {@link JavaApplication} is an {@link Application} for Java-based application processes that use system properties
 * in addition to environment variables as provided by regular {@link Application}s.
 * 
 * @see Application
 * @see JavaApplicationScheme
 * 
 * @author Brian Oliver
 */
public interface JavaApplication extends Application
{

    /**
     * Returns the system properties that were supplied to the {@link JavaApplication} when it was realized.
     * 
     * @return A {@link Map} of name value pairs, each one representing a system property provided to the 
     *         {@link JavaApplication} as -Dname=value parameters when it was realized. 
     */
    public Map<String, String> getSystemProperties();


    /**
     * Returns an individual system property value for a specified system property name, or <code>null</code>
     * if the property is undefined.
     * 
     * @param name The name of the system property
     * 
     * @return The value of the defined system property, or <code>null</code> if undefined.
     */
    public String getSystemProperty(String name);

}
