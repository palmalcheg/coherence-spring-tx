/*
 * File: ParameterScope.java
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

/**
 * <p>A {@link ParameterScope} provides a mechanism to scope and resolve an immutable collection of
 * {@link Parameter}s.</p>
 *
 * <p>For {@link ParameterScope}s that support mutation of the collection, refer to the 
 * {@link MutableParameterScope}.</p>
 *
 * @author Brian Oliver
 */
public interface ParameterScope extends Iterable<Parameter>
{

    /**
     * <p>Returns the specified named parameter.</p>
     * 
     * @param name The name of the parameter
     * @return A {@link Parameter} or <code>null</code> if the parameter is undefined in the {@link ParameterScope}.
     */
    public Parameter getParameter(String name);


    /**
     * <p>Determines if the specified {@link Parameter} is defined with in the {@link ParameterScope}.</p>
     * @param name The name of the {@link Parameter}
     * 
     * @return <code>true</code> if the parameter is defined in the {@link ParameterScope}, 
     *         <code>false</code> otherwise.
     */
    public boolean isDefined(String name);
}
