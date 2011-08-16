/*
 * File: AbstractJavaApplicationScheme.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * An {@link AbstractJavaApplicationScheme} is the base implementation for {@link JavaApplicationScheme}s.
 *
 * @param <A> The type of {@link JavaApplication} that the {@link AbstractJavaApplicationScheme} will create.
 * @param <T> The type of the {@link JavaApplicationScheme} from which default configuration may be retrieved.
 * 
 * @author Brian Oliver
 */
public abstract class AbstractJavaApplicationScheme<A extends JavaApplication, T extends JavaApplicationScheme<A, ?>>
        extends AbstractApplicationScheme<A, T> implements JavaApplicationScheme<A, T>
{

    /**
     * The class name for the Java application.
     */
    private String applicationClassName;

    /**
     * The class path for the Java application.
     */
    private String classPath;

    /**
     * The JVM options.
     */
    private ArrayList<String> options;

    /**
     * The system properties for the application.
     */
    private PropertiesScheme systemPropertiesScheme;


    /**
     * Standard Constructor (with class path).
     * 
     * @param applicationClassName The fully qualified class name of the Java application
     * @param classPath The class path for the Java application
     */
    public AbstractJavaApplicationScheme(String applicationClassName,
                                         String classPath)
    {
        this.applicationClassName = applicationClassName;
        this.classPath = classPath;
        this.options = new ArrayList<String>();
        this.systemPropertiesScheme = new PropertiesScheme();
    }


    /**
     * Standard Constructor.
     * 
     * @param applicationClassName The fully qualified class name of the Java application
     */
    public AbstractJavaApplicationScheme(String applicationClassName)
    {
        this(applicationClassName, System.getProperty("java.class.path"));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesScheme getSystemPropertiesScheme()
    {
        return systemPropertiesScheme;
    }


    /**
     * Sets the class path for the Java application.
     * 
     * @param classPath
     * @return The resulting {@link AbstractJavaApplicationScheme}
     */
    @SuppressWarnings("unchecked")
    public T withClassPath(String classPath)
    {
        this.classPath = classPath;
        return (T) this;
    }


    @SuppressWarnings("unchecked")
    public T withSystemProperty(String name,
                                Object value)
    {
        systemPropertiesScheme.setProperty(name, value);
        return (T) this;
    }


    @SuppressWarnings("unchecked")
    public T withSystemProperties(PropertiesScheme systemProperties)
    {
        systemPropertiesScheme.withProperties(systemProperties);
        return (T) this;
    }


    /**
     * Adds a JVM Option to use when starting the Java application.
     * 
     * @param option The JVM option
     */
    public void addOption(String option)
    {
        //drop the "-" if specified
        options.add(option.startsWith("-") ? option.substring(1) : option);
    }


    /**
     * Adds a JVM Option to use when starting the Java application.
     * 
     * @param option The JVM option
     * @return The resulting {@link AbstractJavaApplicationScheme}
     */
    @SuppressWarnings("unchecked")
    public T withOption(String option)
    {
        addOption(option);
        return (T) this;
    }


    /**
     * Starts the Java {@link Process} using the configuration established by the 
     * {@link AbstractJavaApplicationScheme}.
     * 
     * @param parentScheme The optional parent {@link JavaApplicationScheme} from which base.   
     * 
     * @return A {@link Process} representing the the Java application started.
     * 
     * @throws IOException Thrown if a problem occurs while starting the application.
     */
    @Override
    @SuppressWarnings("unchecked")
    public A realize(T parentScheme) throws IOException
    {
        //construct the command to start the Java process
        ProcessBuilder processBuilder = new java.lang.ProcessBuilder("java");
        processBuilder.redirectErrorStream(true);

        //add the options
        for (String option : options)
        {
            processBuilder.command().add("-" + option);
        }

        //realize the environment variables
        processBuilder.environment().clear();
        processBuilder.environment().put("CLASSPATH", classPath);
        processBuilder.environment().putAll(
            getEnvironmentVariablesScheme().realize(
                parentScheme == null ? null : parentScheme.getEnvironmentVariablesScheme()));

        //realize the system properties
        Map<String, String> systemProperties = systemPropertiesScheme.realize(parentScheme == null ? null : parentScheme
            .getSystemPropertiesScheme());

        for (String name : systemProperties.keySet())
        {
            processBuilder.command().add("-D" + name + "=" + systemProperties.get(name));
        }

        //add the applicationClassName to the command
        processBuilder.command().add(applicationClassName);

        //add the arguments 
        for (String argument : getArguments())
        {
            processBuilder.command().add(argument);
        }

        //start the process
        return (A) new JavaConsoleApplication(processBuilder.start(), processBuilder.environment(), systemProperties);
    }
}
