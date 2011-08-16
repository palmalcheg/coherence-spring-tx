/*
 * File: AbstractApplication.java
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;

/**
 * An {@link AbstractApplication} is a base implementation for an {@link Application} that 
 * uses a java.lang.{@link Process} as a means of controlling the said {@link Application} 
 * at the operating system level.
 *
 * @author Brian Oliver
 */
public abstract class AbstractApplication implements Application
{

    /**
     * The {@link Process} representing the runtime {@link Application}.
     */
    private Process process;

    /**
     * The environment variables used when establishing the {@link Application}.
     */
    private Map<String, String> environmentVariables;


    /**
     * Standard Constructor
     * 
     * @param process The {@link Process} representing the {@link Application}
     * 
     * @param environmentVariables The environment variables used when establishing the {@link Application}
     */
    public AbstractApplication(Process process,
                               Map<String, String> environmentVariables)
    {
        this.process = process;
        this.environmentVariables = environmentVariables;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getEnvironmentVariables()
    {
        return environmentVariables;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy()
    {
        process.destroy();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void redirectOutput(final PrintStream stream,
                               final String prefix)
    {
        new Thread(new Runnable()
        {

            public void run()
            {
                long lineNumber = 1;

                try
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(
                        process.getInputStream())));
                    while (true)
                    {
                        String line = reader.readLine();
                        if (line == null)
                            break;
                        stream.printf("%s %4d: %s\n", prefix, lineNumber++, line);
                    }
                }
                catch (Exception exception)
                {
                    //deliberately empty as we safely assume exceptions  
                    //are always due to process termination.
                }

                System.out.printf("%s %4d: (terminated)\n", prefix, lineNumber++);
            }
        }).start();
    }
}
