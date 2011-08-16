/*
 * File: ReflectiveScheme.java
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

/**
 * <p>A {@link ReflectiveScheme} is a {@link Scheme} that provides runtime type information about 
 * the infrastructure, classes, resources etc that may be produced when the said {@link Scheme} is 
 * realized.</p>
 *
 * <p>This interface essentially provides us with the ability to determine the actual type of objects
 * a {@link Scheme} may realize at runtime, without needing to realize a scheme to do so, thus working 
 * around the problem of type-erasure when using Java Generics.</p>.
 *
 * @author Mark Johnson
 * @author Brian Oliver
 */
public interface ReflectiveScheme extends Scheme
{

    /**
     * <p>Returns if the implementing {@link Scheme} may realize instances of the specified {@link Class}.</p>
     * 
     * @param clazz The {@link Class} that we expect may be realized by the {@link Scheme}
     * @return <code>true</code> if the specified {@link Class} may be realized by the {@link Scheme}.
     */
    public boolean realizesClassOf(Class<?> clazz);
}
