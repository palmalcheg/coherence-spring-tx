/*
 * File: AbstractAsynchronousEventProcessor.java
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
package com.oracle.coherence.common.events.processing;

import java.util.concurrent.ExecutorService;

import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.dispatching.EventDispatcher;
import com.oracle.coherence.environment.Environment;
import com.tangosol.net.CacheFactory;

/**
 * <p>An {@link AbstractAsynchronousEventProcessor} provides an abstract implementation of an {@link EventProcessor}
 * that will aysnchronously process an event (on another thread), instead of the dispatching thread.</p>
 *
 * @author Brian Oliver
 */
public abstract class AbstractAsynchronousEventProcessor<E extends Event> implements EventProcessor<E>
{

    /**
     * {@inheritDoc}
     */
    public final void process(final EventDispatcher eventDispatcher,
                              final E event)
    {
        //schedule the event to be processed later on another thread provided by the executor service
        Environment environment = (Environment) CacheFactory.getConfigurableCacheFactory();
        environment.getResource(ExecutorService.class).submit(new Runnable()
        {

            public void run()
            {
                processLater(eventDispatcher, event);
            }
        });
    }


    /**
     * <p>Process the {@link Event} raised by the specified {@link EventDispatcher}.</p>
     * 
     * <p>NOTE: This method is invoked by a thread different to the thread that raised the {@link Event}.  
     * The raising thread does not block, and thus this allows asynchronous processing of events.</p>
     * 
     * @param eventDispatcher The {@link EventDispatcher} that raised the {@link Event}
     * @param event The {@link Event}.
     */
    public abstract void processLater(EventDispatcher eventDispatcher,
                                      E event);
}
