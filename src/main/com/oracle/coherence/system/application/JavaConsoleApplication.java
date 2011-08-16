/*
 * File: JavaConsoleApplication.java
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
 * A {@link JavaConsoleApplication} is an implementation of a {@link JavaApplication} that has
 * a Console (with standard output, standard error and standard input streams).  
 *
 * @author Brian Oliver
 */
public class JavaConsoleApplication extends AbstractApplication implements JavaApplication
{

    /**
     * The System Properties used to create the underlying {@link Process} represented by 
     * the {@link JavaConsoleApplication}.
     */
    private Map<String, String> systemProperties;


    /**
     * Standard Constructor.
     * 
     * @param process The {@link Process} representing the {@link JavaApplication}.
     * 
     * @param environmentVariables The environment variables used when starting the {@link JavaApplication}.
     * 
     * @param systemProperties The system properties provided to the {@link JavaApplication}
     */
    public JavaConsoleApplication(Process process,
                                  Map<String, String> environmentVariables,
                                  Map<String, String> systemProperties)
    {
        super(process, environmentVariables);

        this.systemProperties = systemProperties;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getSystemProperties()
    {
        return systemProperties;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemProperty(String name)
    {
        return systemProperties.get(name);
    }
}
