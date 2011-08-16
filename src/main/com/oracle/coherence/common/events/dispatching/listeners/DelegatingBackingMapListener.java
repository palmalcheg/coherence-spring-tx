/*
 * File: DelegatingBackingMapListener.java
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

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.events.EntryEvent;
import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryArrivedEvent;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryDepartedEvent;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryEvictedEvent;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryInsertedEvent;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryRemovedEvent;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryStoredEvent;
import com.oracle.coherence.common.events.backingmap.BackingMapEntryUpdatedEvent;
import com.oracle.coherence.common.events.dispatching.EventDispatcher;
import com.oracle.coherence.common.events.processing.EventProcessor;
import com.oracle.coherence.common.events.processing.EventProcessorFactory;
import com.oracle.coherence.common.events.processing.annotations.EventProcessorFor;
import com.oracle.coherence.common.events.processing.annotations.SupportsEventProcessing;
import com.oracle.coherence.environment.Environment;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.CacheEvent;
import com.tangosol.util.Base;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

/**
 * <p>A {@link DelegatingBackingMapListener} is a Server-Side Backing {@link MapListener} that normalizes and dispatches
 * {@link EntryEvent}s to {@link EventProcessor}s for processing.</p>
 * 
 * <p>The {@link EventProcessor} to use to process the {@link EntryEvent}s is determined by looking at the 
 * {@link java.util.Map.Entry} on which the said {@link EntryEvent}s have occurred.</p>
 * 
 * <p>If an {@link java.util.Map.Entry} implements the {@link EventProcessor} interface then the {@link EntryEvent}s are 
 * dispatched to  the {@link java.util.Map.Entry} itself for processing.</p>
 * 
 * <p>Note: {@link com.oracle.coherence.common.events.processing.LifecycleAwareEntry}s are {@link EventProcessor}s, 
 * meaning you can simply make {@link java.util.Map.Entry}s implement {@link com.oracle.coherence.common.events.processing.LifecycleAwareEntry} 
 * to support "self" processing of {@link EntryEvent}s.</p>
 * 
 * <p>If an {@link java.util.Map.Entry} implements the {@link EventProcessorFactory} interface, then the {@link Event}s 
 * are dispatched to the {@link EventProcessor}s provided by the said {@link EventProcessorFactory} implementation on 
 * the {@link java.util.Map.Entry}.</p>
 * 
 * <p>If an {@link java.util.Map.Entry} is annotated with {@link SupportsEventProcessing} then the {@link Event}s are 
 * dispatched to the appropriate method annotated with {@link EventProcessorFor} on the said {@link java.util.Map.Entry}.</p>
 * 
 * @author Brian Oliver
 */
public class DelegatingBackingMapListener implements MapListener
{
    /**
     * The logger to use.
     */
    private static final Logger logger = Logger.getLogger(DelegatingBackingMapListener.class.getName());

    /**
     * <p>The {@link BackingMapManagerContext} that owns this listener. (all Backing {@link MapListener}s require a
     * {@link BackingMapManagerContext} )</p>
     */
    private BackingMapManagerContext backingMapManagerContext;

    /**
     * <p>The name of the {@link NamedCache} on which this {@link DelegatingBackingMapListener} is registered for
     * server-side processing of {@link Event}s.</p>
     */
    private String cacheName;


    /**
     * <p>Standard Constructor</p>
     * 
     * <p>NOTE: The {@link BackingMapManagerContext} will be injected by Coherence during initialization and
     * construction of the {@link com.tangosol.net.BackingMapManager}.</p>
     * 
     * @param backingMapManagerContext The {@link BackingMapManagerContext} associated with this listener
     * @param cacheName                The cache name associated with this listener
     */
    public DelegatingBackingMapListener(BackingMapManagerContext backingMapManagerContext,
                                        String cacheName)
    {
        this.backingMapManagerContext = backingMapManagerContext;
        this.cacheName = cacheName;
    }


    /**
     * <p>Returns the name of the {@link com.tangosol.net.NamedCache} on which the {@link DelegatingBackingMapListener} is
     * registered.</p>
     * 
     * @return the name of the {@link com.tangosol.net.NamedCache} on which the {@link DelegatingBackingMapListener} is
     * registered
     */
    public String getCacheName()
    {
        return cacheName;
    }


    /**
     * <p>The {@link BackingMapManagerContext} in which the Backing {@link MapListener} is operating.</p>
     * 
     * @return {@link BackingMapManagerContext}
     */
    public BackingMapManagerContext getContext()
    {
        return backingMapManagerContext;
    }


    /**
     * <p>Return the {@link EventDispatcher} that should be used to dispatch {@link Event}s.</p>
     * 
     * @return the {@link EventDispatcher} that should be used to dispatch {@link Event}s
     */
    public EventDispatcher getEventDispatcher()
    {
        // use an Environment to locate the EventDispatcher resource
        ConfigurableCacheFactory ccf = CacheFactory.getCacheFactoryBuilder().getConfigurableCacheFactory(
            getContext().getClassLoader());
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
     * <p>Determines whether the given decoration has been removed from the event's new value, i.e., the decoration
     * exists on the old value but not on the new.</p>
     * 
     * @param evt           The event to check
     * @param nDecorationId The decoration to look for
     * 
     * @return true if the decoration has been removed from the event's new value
     */
    protected boolean isDecorationRemoved(MapEvent evt,
                                          int nDecorationId)
    {
        Binary binOldValue = (Binary) evt.getOldValue();
        Binary binNewValue = (Binary) evt.getNewValue();
        BackingMapManagerContext ctx = getContext();
        return (binOldValue != null && ctx.isInternalValueDecorated(binOldValue, nDecorationId) && !ctx
            .isInternalValueDecorated(binNewValue, nDecorationId));
    }


    /**
     * {@inheritDoc}
     */
    public void entryInserted(MapEvent mapEvent)
    {
        EntryEvent event;
        if (getContext().isKeyOwned(mapEvent.getKey()))
        {
            // Coherence marks an Entry that needs to be persisted with the DECO_STORE flag. Once finished it's removed.
            // We use this to determine if it's an EntryStoredEvent or not
            if (isDecorationRemoved(mapEvent, ExternalizableHelper.DECO_STORE))
            {
                event = new BackingMapEntryStoredEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
                    .getNewValue());
            }
            else
            {
                event = new BackingMapEntryInsertedEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
                    .getNewValue());
            }
        }
        else
        {
            event = new BackingMapEntryArrivedEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
                .getNewValue());
        }

        scheduleProcessor(event);
    }


    /**
     * {@inheritDoc}
     */
    public void entryUpdated(MapEvent mapEvent)
    {
        EntryEvent event = new BackingMapEntryUpdatedEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
            .getOldValue(), mapEvent.getNewValue());

        scheduleProcessor(event);
    }


    /**
     * {@inheritDoc}
     */
    public void entryDeleted(MapEvent mapEvent)
    {
        EntryEvent event;
        if (backingMapManagerContext.isKeyOwned(mapEvent.getKey()))
        {
            if (mapEvent instanceof CacheEvent && ((CacheEvent) mapEvent).isSynthetic())
            {
                event = new BackingMapEntryEvictedEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
                    .getOldValue());
            }
            else
            {
                event = new BackingMapEntryRemovedEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
                    .getOldValue());
            }
        }
        else
        {
            event = new BackingMapEntryDepartedEvent(getContext(), getCacheName(), mapEvent.getKey(), mapEvent
                .getOldValue());
        }

        scheduleProcessor(event);
    }


    /**
     * <p>Schedule the processing of an event.</p>
     * 
     * @param event The event to schedule for processing
     */
    @SuppressWarnings("unchecked")
    private void scheduleProcessor(Event event)
    {
        if (event instanceof EntryEvent)
        {
            EntryEvent entryEvent = (EntryEvent) event;
            if (entryEvent.getEntry().getValue() instanceof EventProcessor)
            {
                getEventDispatcher().dispatchEvent(event, (EventProcessor) entryEvent.getEntry().getValue());
            }
            else if (entryEvent.getEntry().getValue() instanceof EventProcessorFactory)
            {
                EventProcessor<Event> eventProcessor = ((EventProcessorFactory) entryEvent.getEntry().getValue())
                    .getEventProcessor(entryEvent);

                if (eventProcessor == null)
                {
                    //SKIP: EventProcessorFactory returned nothing... so there is nothing to do
                }
                else
                {
                    getEventDispatcher().dispatchEvent(event, eventProcessor);
                }
            }
            else
            {
                Class entryClass = entryEvent.getEntry().getValue().getClass();
                if (entryClass.getAnnotation(SupportsEventProcessing.class) == null)
                {
                    //SKIP: the entry doesn't support event processing in any way
                }
                else
                {
                    // attempt to find a method in the entryClass that can process the event
                    Method eventProcessorMethod = null;
                    for (Method method : entryClass.getDeclaredMethods())
                    {
                        if (method.getParameterTypes().length == 2)
                        {
                            EventProcessorFor eventProcessorFor = method.getAnnotation(EventProcessorFor.class);
                            if (eventProcessorFor != null)
                            {
                                for (Class<? extends Event> eventClass : eventProcessorFor.events())
                                {
                                    if (eventClass.isAssignableFrom(entryEvent.getClass()))
                                    {
                                        eventProcessorMethod = method;
                                        break;
                                    }
                                }
                            }
                        }

                        // we terminate searching for an event processing method when we've found one that matches
                        if (eventProcessorMethod != null)
                        {
                            break;
                        }
                    }

                    if (eventProcessorMethod != null)
                    {
                        getEventDispatcher().dispatchEvent(entryEvent,
                            (EventProcessor) new MethodBasedEventProcessor(eventProcessorMethod));
                    }
                }
            }

        }
        else
        {
            //SKIP: we don't yet support non-EntryEvents. ie: PartitionEvents.
        }
    }


    /**
     * <p>A {@link MethodBasedEventProcessor} is useful for processing {@link Event}s using a specific {@link Method}
     * .</p>
     */
    private static class MethodBasedEventProcessor implements EventProcessor<EntryEvent>
    {

        /**
         * <p>The {@link Method} that can process the {@link Event}.</p>
         */
        private Method method;


        /**
         * <p>Standard Constructor.</p>
         * 
         * @param method the {@link Method} to execute
         */
        public MethodBasedEventProcessor(Method method)
        {
            this.method = method;
        }


        /**
         * {@inheritDoc}
         */
        public void process(EventDispatcher eventDispatcher,
                            EntryEvent event)
        {
            try
            {
                method.invoke(event.getEntry().getValue(), eventDispatcher, event);
            }
            catch (Exception  exception)
            {
                logger.log(Level.SEVERE, "Exception in process method ", exception);
                throw Base.ensureRuntimeException(exception);
            }

        }
    }

}
