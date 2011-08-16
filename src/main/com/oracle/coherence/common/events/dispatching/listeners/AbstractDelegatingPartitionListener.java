/*
 * File: AbstractDelegatingPartitionListener.java
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
package com.oracle.coherence.common.events.dispatching.listeners;

import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.dispatching.EventDispatcher;
import com.oracle.coherence.common.events.partition.PartitionArrivingEvent;
import com.oracle.coherence.common.events.partition.PartitionDepartingEvent;
import com.oracle.coherence.common.events.partition.PartitionEvent;
import com.oracle.coherence.common.events.partition.PartitionLostEvent;
import com.oracle.coherence.common.events.partition.PartitionRecoveringEvent;
import com.oracle.coherence.common.events.processing.EventProcessor;
import com.oracle.coherence.common.events.processing.EventProcessorFactory;
import com.oracle.coherence.environment.Environment;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.PartitionedService;
import com.tangosol.net.partition.PartitionListener;

/**
 * <p>An {@link AbstractDelegatingPartitionListener} dispatches {@link PartitionEvent}s (using an
 * {@link EventDispatcher}) to the {@link EventProcessor}s returned by the {@link EventProcessorFactory} implementation
 * provided by sub-classes of this abstract class.</p>
 * 
 * @author Brian Oliver
 */
public abstract class AbstractDelegatingPartitionListener implements PartitionListener,
        EventProcessorFactory<PartitionEvent>
{

    /**
     * <p>Standard Constructor.</p>
     */
    public AbstractDelegatingPartitionListener()
    {
    }

    /**
     * <p>Determine the suitable {@link EventDispatcher} for the specified {@link PartitionedService}.</p>
     * 
     * @param partitionedService The {@link PartitionedService} to use
     * 
     * @return the requested {@link EventDispatcher}
     */
    protected EventDispatcher getEventDispatcher(PartitionedService partitionedService)
    {
        // use an Environment to locate the EventDispatcher resource
        ConfigurableCacheFactory ccf = CacheFactory.getCacheFactoryBuilder().getConfigurableCacheFactory(
            partitionedService.getContextClassLoader());
        if (ccf instanceof Environment)
        {
            Environment environment = (Environment) ccf;
            EventDispatcher eventDispatcher = environment.getResource(EventDispatcher.class);
            if (eventDispatcher == null)
            {
                throw new RuntimeException(
                    "Failed to locate the EventDispatcher resource.  Your application appears to be "
                            + "incorrectly configured or your Environment does not support EventDispatching");
            }
            else
            {
                return eventDispatcher;
            }
        }
        else
        {
            throw new RuntimeException(
                "Can not locate the EventDispatcher resource as the ConfigurableCacheFactory does "
                        + "not support Environments. At a minimum you should configure your application to use "
                        + "the ExtensibleEnvironment.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void onPartitionEvent(com.tangosol.net.partition.PartitionEvent partitionEvent)
    {
        PartitionEvent event;
        switch (partitionEvent.getId())
        {
            case com.tangosol.net.partition.PartitionEvent.PARTITION_LOST:
                event = new PartitionLostEvent(partitionEvent);
                break;

            case com.tangosol.net.partition.PartitionEvent.PARTITION_RECEIVE_BEGIN:
            case com.tangosol.net.partition.PartitionEvent.PARTITION_RECEIVE_COMMIT:
                if (partitionEvent.getFromMember() == null)
                {
                    event = new PartitionRecoveringEvent(partitionEvent);
                }
                else
                {
                    event = new PartitionArrivingEvent(partitionEvent);
                }
                break;

            case com.tangosol.net.partition.PartitionEvent.PARTITION_TRANSMIT_BEGIN:
            case com.tangosol.net.partition.PartitionEvent.PARTITION_TRANSMIT_COMMIT:
            case com.tangosol.net.partition.PartitionEvent.PARTITION_TRANSMIT_ROLLBACK:
                event = new PartitionDepartingEvent(partitionEvent);
                break;

            default:
                event = null;
        }

        if (event != null)
        {
            EventProcessor<PartitionEvent> eventProcessor = getEventProcessor(event);
            getEventDispatcher(event.getService()).dispatchEvent((Event) event, (EventProcessor) eventProcessor);
        }
    }
}
