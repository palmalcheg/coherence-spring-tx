/*
 * File: Expression.java
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
package com.oracle.coherence.configuration.expressions;

import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.environment.Environment;

/**
 * <p>A {@link Expression} represents some user-defined expression that may be evaluated at runtime.</p>
 *
 * @author Brian Oliver
 */
public interface Expression<T>
{

    /**
     * <p>Evaluates the expression to produce a resulting value.</p>
     * 
     * @param environment The {@link Environment} in which to evaluate the {@link Expression}
     * @param classLoader The {@link ClassLoader} for loading/accessing new classes should they be required
     * @param parameterScope The {@link ParameterScope} that may be used to resolve parameter values occurring in the 
     *                       {@link Expression}
     * 
     * @return The result of evaluating the expression
     * 
     * @throws ClassCastException When a type conversion or coercion fails while evaluating the expression
     * @throws ClassNotFoundException When an attempt to load a class fails while evaluating the expression
     * @throws IllegalArgumentException When an argument used in the expression is not resolvable
     */
    public T evaluate(Environment environment,
                      ClassLoader classLoader,
                      ParameterScope parameterScope);
}
