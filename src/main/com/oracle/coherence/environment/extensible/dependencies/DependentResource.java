/*
 * File: DependentResource.java
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

import java.util.Set;

import com.oracle.coherence.environment.Environment;

/**
 * <p>A {@link DependentResource} is an {@link Environment} registered resource that has an explicitly defined
 * {@link Set} of dependencies (represented as {@link DependencyReference}s) that must be 
 * "satisfied" (started) before the said resource should be used.  Should one or more dependencies
 * be stopped (after they were all previously started), the dependencies for the {@link DependentResource}
 * are said to be "violated".</p>
 *
 * @see com.oracle.coherence.common.events.lifecycle.LifecycleEvent
 * 
 * @author Brian Oliver
 */
public interface DependentResource
{
    /**
     * <p>Returns the {@link Set} of {@link DependencyReference}s representing the dependencies
     * that <strong>all</strong>must be started before the {@link DependentResource} is satisfied.</p>
     * 
     * @return The {@link Set} of {@link DependencyReference}s representing required dependencies
     * (or <code>null</code>) if the {@link DependentResource} has no dependencies.
     */
    public Set<DependencyReference> getDependencyReferences();


    /**
     * <p>The {@link #onDependenciesSatisfied(Environment)} is called when <strong>all</strong> of the 
     * specified {@link DependencyReference}s in {@link #getDependencyReferences()} {@link Set}
     * have been started.</p>
     * 
     * @see com.oracle.coherence.common.events.lifecycle.LifecycleStartedEvent
     * 
     * @param environment The {@link Environment} in which the dependencies have been satisfied.
     */
    public void onDependenciesSatisfied(Environment environment);


    /**
     * <p>The {@link #onDependenciesViolated(Environment)} is called when the <strong>first</strong> 
     * of one of the specified {@link DependencyReference}s in {@link #getDependencyReferences()} {@link Set}
     * of the <strong>previously satisfied</strong> {@link DependentResource}, has been stopped,
     * thus meaning the {@link DependentResource} has at least one of their dependencies violated.</p>
     * 
     * @see com.oracle.coherence.common.events.lifecycle.LifecycleStoppedEvent
     * 
     * @param environment The {@link Environment} in which the dependencies have been violated.
     */
    public void onDependenciesViolated(Environment environment);
}
