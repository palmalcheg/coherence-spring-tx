/*
 * File: MutableParameterScope.java
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
 * <p>A {@link MutableParameterScope} is {@link ParameterScope} that allows mutation/addition/modification
 * of the defined {@link Parameter}s.</p>
 *
 * @author Brian Oliver
 */
public interface MutableParameterScope extends ParameterScope
{

    /**
     * <p>Adds the specified {@link Parameter} to the {@link MutableParameterScope}.</p>
     * 
     * @param parameter The {@link Parameter}
     */
    public void addParameter(Parameter parameter);
}
