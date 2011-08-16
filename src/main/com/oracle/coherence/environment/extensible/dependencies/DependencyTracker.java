/*
 * File: DependencyTracker.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.events.dispatching.EventDispatcher;
import com.oracle.coherence.common.events.lifecycle.LifecycleEvent;
import com.oracle.coherence.common.events.lifecycle.LifecycleStartedEvent;
import com.oracle.coherence.common.events.lifecycle.LifecycleStoppedEvent;
import com.oracle.coherence.common.events.processing.AbstractAsynchronousEventProcessor;
import com.oracle.coherence.common.events.processing.EventProcessor;
import com.oracle.coherence.environment.Environment;

/**
 * <p>A {@link DependencyTracker} is an {@link EventProcessor} that will track the life-cycle state of a set of 
 * dependencies for a {@link DependentResource} with in an {@link Environment}.</p>
 * 
 * @see DependentResource
 * @see DependencyReference
 * 
 * @author Brian Oliver
 */
public class DependencyTracker extends AbstractAsynchronousEventProcessor<LifecycleEvent<?>>
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static Logger logger = Logger.getLogger(DependencyTracker.class.getName());

    /**
     * <p>The {@link Environment} that created this {@link DependencyTracker}.</p>
     */
    private Environment environment;

    /**
     * <p>The {@link DependentResource} for which we'll be tracking the lifecycle
     * of dependencies (represented as {@link DependencyReference}s).</p>
     */
    private DependentResource dependentResource;


    /**
     * <p>The {@link Status} enumeration captures the possible states of a dependency.</p>
     */
    private enum Status
    {
        /**
         * <p>A dependency has started.</p>
         */
        Started,

        /**
         * <p>A dependency has stopped.</p>
         */
        Stopped
    };

    /**
     * <p>The map of dependency states (true = started, false = stopped).</p>
     */
    private HashMap<DependencyReference, Status> dependenciesStatus;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param environment The {@link Environment} in which the {@link DependencyTracker} is tracking resources
     *                    life-cycles.
     * @param dependentResource The {@link DependentResource} whose dependencies require tracking.
     */
    public DependencyTracker(Environment environment,
                             DependentResource dependentResource)
    {
        this.environment = environment;
        this.dependentResource = dependentResource;

        // create a structure to track the initial status of the dependencies of
        // the resource

        // in the future we should actually resolve and ask the dependencies if they've 
        // already started instead of just assuming they are stopped when we register.
        this.dependenciesStatus = new HashMap<DependencyReference, Status>();
        for (DependencyReference dependencyReference : dependentResource.getDependencyReferences())
        {
            this.dependenciesStatus.put(dependencyReference, Status.Stopped);
        }

        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "Created DependencyTracker for {0}\n", dependentResource);
    }


    /**
     * <p>Returns the {@link DependentResource} for which we are tracking dependencies.</p>
     * 
     * @return The {@link DependentResource} for which we are tracking dependencies.
     */
    public DependentResource getDependentResource()
    {
        return dependentResource;
    }


    /**
     * <p>Determines if all of the dependencies specified by the {@link DependentResource}
     * {@link DependentResource#getDependencyReferences()} have been started and are still 
     * running.</p>
     * 
     * @return <code>true</code> if all of the dependencies for the {@link DependentResource}
     *         have started and are running.
     */
    public boolean areDependenciesSatisfied()
    {
        boolean isSatisfied = true;
        for (Iterator<Status> statuses = dependenciesStatus.values().iterator(); statuses.hasNext() && isSatisfied;)
        {
            isSatisfied = isSatisfied && statuses.next() == Status.Started;
        }
        return isSatisfied;
    }


    /**
     * <p>Using the provided {@link LifecycleEvent}, update the current
     * {@link DependencyReference} state for our {@link DependentResource}.</p>
     * 
     * @param eventDispatcher The {@link EventDispatcher} that raised the {@link LifecycleEvent}.
     * @param event The {@link LifecycleEvent} that was raised.
     */
    public void processLater(EventDispatcher eventDispatcher,
                             LifecycleEvent<?> event)
    {
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "DependencyTracker for {0}: Received Lifecycle Event {1}\n", new Object[] {
                    dependentResource, event });

        // determine which of the tracked DependencyReferences match the
        // LifecycleEvent
        DependencyReference dependencyReference = null;
        for (Iterator<DependencyReference> dependencyReferences = dependenciesStatus.keySet().iterator(); dependencyReferences
            .hasNext()
                && dependencyReference == null;)
        {
            dependencyReference = dependencyReferences.next();
            if (!dependencyReference.isReferencing(event.getSource()))
            {
                dependencyReference = null;
            }
        }

        // we've found one that we're interested in
        if (dependencyReference != null)
        {
            boolean isCurrentlySatisfied = areDependenciesSatisfied();

            if (event instanceof LifecycleStartedEvent<?>)
            {
                // track that dependency has started
                dependenciesStatus.put(dependencyReference, Status.Started);

                if (isCurrentlySatisfied)
                {
                    // WARNING: we should not get into this state as one of the previously thought of satisfied 
                    // dependencies have been started again. just ignore as we're still running
                    logger
                        .log(
                            Level.WARNING,
                            "DependencyTracker is already satisfied, still a LifeCycleStartedEvent was received {0}: Received Lifecycle Event {1}\n",
                            new Object[] { dependentResource, event });
                }
                else
                {
                    // are all of the dependencies now satisfied?
                    // if so we need to tell the DependentResource
                    // and then we dispatch a LifeCycleStartedEvent to wake all
                    // our dependents
                    if (areDependenciesSatisfied())
                    {
                        //TODO: This must be executed on another thread!
                        dependentResource.onDependenciesSatisfied(environment);
                    }
                }
            }
            else if (event instanceof LifecycleStoppedEvent<?>)
            {
                // track that the dependency has stopped
                dependenciesStatus.put(dependencyReference, Status.Stopped);

                if (isCurrentlySatisfied)
                {
                    // tell the DependentResource that the first of it's
                    // dependencies
                    // have now been violated
                    environment.getResource(EventDispatcher.class).dispatchEvent(
                        new LifecycleStoppedEvent<DependentResource>(dependentResource));

                    //TODO: This must be executed on another thread!
                    dependentResource.onDependenciesViolated(environment);
                }
            }
            else
            {
                // WARNING: the LifecycleEvent type is unknown - we don't know what to do here
                logger.log(Level.WARNING,
                    "DependencyTracker received an unknown LifeCycleEvent for {0}: Received Lifecycle Event {1}\n",
                    new Object[] { dependentResource, event });
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dependentResource == null) ? 0 : dependentResource.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DependencyTracker other = (DependencyTracker) obj;
        if (dependentResource == null)
        {
            if (other.dependentResource != null)
                return false;
        }
        else if (!dependentResource.equals(other.dependentResource))
            return false;
        return true;
    }
}
