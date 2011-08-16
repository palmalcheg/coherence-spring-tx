/*
 * File: AbstractApplicationScheme.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An {@link AbstractApplicationScheme} is a base implementation of an {@link ApplicationScheme}s that provides
 * access and management of environment variables, together with arguments for the said {@link ApplicationScheme}s.
 *
 * @param <A> The type of {@link Application} that the {@link AbstractApplicationScheme} will create.
 * @param <T> The type of the {@link AbstractApplicationScheme} from which default configuration may be retrieved.
 *
 * @see Application
 * @see ApplicationScheme
 * 
 * @author Brian Oliver
 */
public abstract class AbstractApplicationScheme<A extends Application, T extends ApplicationScheme<A, ?>> implements
        ApplicationScheme<A, T>
{

    /**
     * The {@link PropertiesScheme} containing the environment variables to be used when realizing 
     * the {@link Application}.
     */
    private PropertiesScheme environmentVariablesScheme;

    /**
     * <p>The arguments for the application.</p>
     */
    private ArrayList<String> arguments;


    /**
     * Standard Constructor.
     */
    public AbstractApplicationScheme()
    {
        environmentVariablesScheme = new PropertiesScheme();
        arguments = new ArrayList<String>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesScheme getEnvironmentVariablesScheme()
    {
        return environmentVariablesScheme;
    }


    /**
     * Sets the specified environment variable to use an {@link Iterator} from which to retrieve it's values
     * when the {@link ApplicationScheme} is realized.
     * 
     * @param name The name of the environment variable.
     * 
     * @param iterator An {@link Iterator} providing values for the environment variable.
     * 
     * @return The modified {@link ApplicationScheme} (so that we can perform method chaining).
     */
    @SuppressWarnings("unchecked")
    public T withEnvironmentVariable(String name,
                                     Iterator<?> iterator)
    {
        environmentVariablesScheme.setProperty(name, iterator);
        return (T) this;
    }


    /**
     * Sets the specified environment variable to the specified value that is then used when the 
     * {@link ApplicationScheme} is realized.
     * 
     * @param name The name of the environment variable.
     * 
     * @param value The value of the environment variable.
     * 
     * @return The modified {@link ApplicationScheme} (so that we can perform method chaining).
     */
    @SuppressWarnings("unchecked")
    public T withEnvironmentVariable(String name,
                                     Object value)
    {
        environmentVariablesScheme.setProperty(name, value);
        return (T) this;
    }


    /**
     * Adds/Overrides the current environment variables with those specified by the {@link PropertiesScheme}. 
     * 
     * @param environmentVariables The environment variables to add/override on the {@link ApplicationScheme}.
     * 
     * @return The modified {@link ApplicationScheme} (so that we can perform method chaining).
     */
    @SuppressWarnings("unchecked")
    public T withEnvironmentVariables(PropertiesScheme environmentVariables)
    {
        environmentVariablesScheme.withProperties(environmentVariables);
        return (T) this;
    }


    /**
     * Clears the currently registered environment variables from the {@link ApplicationScheme}.
     * 
     * @return The modified {@link ApplicationScheme} (so that we can perform method chaining).
     */
    @SuppressWarnings("unchecked")
    public T withNoEnvironmentVariables()
    {
        environmentVariablesScheme.clear();
        return (T) this;
    }


    /**
     * Adds an argument to use when starting the {@link Application}.
     * 
     * @param argument The argument for the {@link Application}.
     */
    public void addArgument(String argument)
    {
        arguments.add(argument);
    }


    /**
     * Returns the list of arguments defined for the {@link Application}.
     * 
     * @return {@link List} of {@link String}s.
     */
    public List<String> getArguments()
    {
        return arguments;
    }


    /**
     * Adds an argument to use when starting the {@link Application}.
     * 
     * @param argument The argument for the {@link Application}.
     * 
     * @return The resulting {@link ApplicationScheme}.
     */
    @SuppressWarnings("unchecked")
    public T withArgument(String argument)
    {
        addArgument(argument);
        return (T) this;
    }
}
