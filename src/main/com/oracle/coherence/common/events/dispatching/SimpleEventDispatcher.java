/*
 * File: SimpleEventDispatcher.java
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
package com.oracle.coherence.common.events.dispatching;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.processing.EventProcessor;
import com.oracle.coherence.environment.Environment;
import com.tangosol.util.Filter;

/**
 * <p>A simple implementation of an {@link EventDispatcher}.</p>
 * 
 * @author Brian Oliver
 */
public class SimpleEventDispatcher implements EventDispatcher
{

    /**
     * <p>The {@link Environment} in which the {@link EventDispatcher} is operating.</p>
     */
    private Environment environment;

    /**
     * <p>The currently registered {@link EventProcessor}s that will be used to 
     * process {@link Event}s.</p>
     */
    private ConcurrentHashMap<Filter, CopyOnWriteArraySet<EventProcessor<?>>> eventProcessors;

    /**
     * The {@link Logger} to use.
     */
    private static Logger logger = Logger.getLogger(SimpleEventDispatcher.class.getName());


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param environment the {@link Environment} to use
     */
    public SimpleEventDispatcher(Environment environment)
    {
        this.environment = environment;
        this.eventProcessors = new ConcurrentHashMap<Filter, CopyOnWriteArraySet<EventProcessor<?>>>();
    }


    /**
     * {@inheritDoc}
     */
    public Environment getEnvironment()
    {
        return environment;
    }


    /**
     * {@inheritDoc}
     */
    public <E extends Event> void dispatchEvent(E event,
                                                EventProcessor<E> eventProcessor)
    {
        //have the specified event processed with the specified event processor
        if (logger.isLoggable(Level.FINEST))
        {
            logger.log(Level.FINEST, "[Commenced] Processing {0} with {1}", new Object[] { event, eventProcessor });
        }
        ((EventProcessor<E>) eventProcessor).process(this, event);

        if (logger.isLoggable(Level.FINEST))
        {
            logger.log(Level.FINEST, "[Completed] Processing {0} with {1}", new Object[] { event, eventProcessor });
        }
        //dispatch the event to other event processors
        dispatchEvent(event);
    }


    /**
     * {@inheritDoc}
     */
    public <E extends Event> void dispatchEventLater(final E event)
    {
        //schedule the event to be dispatched later using our execution service
        getEnvironment().getResource(ExecutorService.class).execute(new Runnable()
        {

            public void run()
            {
                dispatchEvent(event);
            }
        });

    }


    /**
     * {@inheritDoc}
     */
    public <E extends Event> void registerEventProcessor(Filter filter,
                                                         EventProcessor<E> eventProcessor)
    {
        // while the underlying data-structures are thread-safe, we need to guard against
        // two threads that attempt to add and remove the same filter and event processor simultaneously
        // (as two data-structures are involved, we could get a race)
        synchronized (eventProcessors)
        {
            Set<EventProcessor<?>> eventProcessorSet = eventProcessors.putIfAbsent(filter,
                new CopyOnWriteArraySet<EventProcessor<?>>());
            if (eventProcessorSet == null)
            {
                eventProcessorSet = eventProcessors.get(filter);
            }
            eventProcessorSet.add(eventProcessor);
        }
    }


    /**
     * {@inheritDoc}
     */
    public <E extends Event> void unregisterEventProcessor(Filter filter,
                                                           EventProcessor<E> eventProcessor)
    {
        // while the underlying data-structures are thread-safe, we need to guard against
        // two threads that attempt to add and remove the same filter and event processor simultaneously
        // (as two data-structures are involved, we could get a race)
        synchronized (eventProcessors)
        {
            Set<EventProcessor<?>> eventProcessorSet = eventProcessors.get(filter);
            if (eventProcessorSet != null)
            {
                eventProcessorSet.remove(eventProcessor);

                if (eventProcessorSet.isEmpty())
                {
                    eventProcessors.remove(filter);
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <E extends Event> void dispatchEvent(E event)
    {
        for (Filter filter : eventProcessors.keySet())
        {
            if (filter.evaluate(event))
            {
                for (EventProcessor<?> eventProcessor : eventProcessors.get(filter))
                {
                    if (logger.isLoggable(Level.FINEST))
                    {
                        logger.log(Level.FINEST, "[Commenced] Processing {0} with {1}", new Object[] { event,
                                eventProcessor });
                    }

                    ((EventProcessor<E>) eventProcessor).process(this, event);

                    if (logger.isLoggable(Level.FINEST))
                    {
                        logger.log(Level.FINEST, "[Completed] Processing {0} with {1}", new Object[] { event,
                                eventProcessor });
                    }
                }
            }
        }
    }
}
