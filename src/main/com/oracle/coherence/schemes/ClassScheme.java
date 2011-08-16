/*
 * File: ClassScheme.java
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
package com.oracle.coherence.schemes;

import com.oracle.coherence.configuration.parameters.Parameter;
import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.environment.Environment;

/**
 * <p>A {@link ClassScheme} captures configuration information necessary to realize
 * an instance of a configured class at runtime.</p>
 *
 * @param <T> The type of the class that will be produced by the {@link ClassScheme}
 * 
 * @author Brian Oliver
 */
public interface ClassScheme<T> extends ReflectiveScheme
{

    /**
     * <p>Realizes an instance of the class.</p>
     * 
     * @param environment The {@link Environment} in which the {@link ClassScheme} is operating.
     * @param classLoader The {@link ClassLoader} that should be used to load any resources for the class being realized.
     * @param parameterScope The {@link ParameterScope} containing {@link Parameter}s possibly used by the scheme.
     * 
     * @return An instance of <T>
     */
    public T realize(Environment environment,
                     ClassLoader classLoader,
                     ParameterScope parameterScope);
}
