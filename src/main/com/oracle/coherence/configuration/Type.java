/*
 * File: Type.java
 * 
 * Copyright (c) 2009-2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The {@link Type} annotation is used to declare the expected type of a property
 * so that a {@link Configurator} may perform type checking during configuration.</p>
 *  
 * <p>When a {@link Type} is not declared for a property, a {@link Configurator} will
 * attempt to infer the type by looking at the property method declaration.</p>
 *  
 * @see Configurator 
 * @see Property
 * @see SubType
 *  
 * @author Brian Oliver
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Type
{
    /**
     * <p>Returns the type of the property.</p>
     * 
     * @return A {@link Class} representing the sub-type of the property.
     */
    public Class<?> value();
}
