/*
 * File: DependencyReference.java
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
package com.oracle.coherence.environment.extensible.dependencies;

/**
 * <p>A {@link DependencyReference} captures information about a required object, named cache, service etc
 * that is required for a resource to operate.</p>
 * 
 * <p>The typical use for {@link DependencyReference}s is to define the objects, named caches and/or services
 * that a {@link DependentResource} requires for correct operation.</p>
 * 
 * @author Brian Oliver
 */
public interface DependencyReference
{
    /**
     * <p>Determines if the {@link DependencyReference} is referring to the specified {@link Object}.</p>
     * 
     * @param object The object being checked.
     * 
     * @return <code>true</code> if the {@link DependencyReference} is referring to the specified {@link Object}.
     *         Always returns <code>false</code> if the supplied object is <code>null</code>.
     */
    public boolean isReferencing(Object object);
}
