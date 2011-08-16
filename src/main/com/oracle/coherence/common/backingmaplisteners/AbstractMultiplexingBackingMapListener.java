/*
 * File: AbstractMultiplexingBackingMapListener.java
 * 
 * Copyright (c) 2008-2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.backingmaplisteners;

import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.cache.CacheEvent;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MultiplexingMapListener;
import com.tangosol.util.ObservableMap;

/**
 * <p>The {@link AbstractMultiplexingBackingMapListener} provides an extensible implementation 
 * of the {@link MultiplexingBackingMapListener}.</p>
 * 
 * @author Brian Oliver
 * @author Andy Nguyen 
 */
public abstract class AbstractMultiplexingBackingMapListener extends MultiplexingMapListener implements
        MultiplexingBackingMapListener
{
    /**
     * <p>The {@link BackingMapManagerContext} that owns this listener.  
     * (all Backing {@link MapListener}s require a {@link BackingMapManagerContext})</p>
     */
    private BackingMapManagerContext context;

    /**
     * <p>Standard Constructor</p>
     * 
     * <p>The {@link BackingMapManagerContext} will be injected by Coherence during
     * initialization and construction of the {@link com.tangosol.net.BackingMapManager}.</p>
     * 
     * @param context The BackingMapManagerContext associated with this listener
     */
    public AbstractMultiplexingBackingMapListener(BackingMapManagerContext context)
    {
        this.context = context;
    }

    /**
     * <p>The {@link BackingMapManagerContext} in which the Backing {@link com.tangosol.util.MapListener}
     * is operating.</p>
     * 
     * @return {@link BackingMapManagerContext}
     */
    public BackingMapManagerContext getContext()
    {
        return context;
    }

    /**
     * <p>Determines whether the given decoration has been removed from the event's new
     * value, i.e., the decoration exists on the old value but not on the new.</p>
     * 
     * @param evt           The event to check
     * @param nDecorationId The decoration to look for.
     * 
     * @return true if the decoration has been removed for the new value
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
     * <p>This is the standard {@link MultiplexingMapListener} event handler.  In here
     * we convert the internally formatted event into something a developer would 
     * expect if using a client-side {@link com.tangosol.util.MapListener}.</p>
     * 
     * <p>After converting the internally formatted event, this method calls the abstract  
     * {@link #onBackingMapEvent(MapEvent, Cause)}
     * method that may be used to handle the actual event.</p>
     * 
     * @param mapEvent the MapEvent that has occured
     */
    protected final void onMapEvent(MapEvent mapEvent)
    {

        //determine the underlying cause of the map event
        Cause cause;
        if (context.isKeyOwned(mapEvent.getKey()))
        {
            if (isDecorationRemoved(mapEvent, ExternalizableHelper.DECO_STORE))
            {
                cause = Cause.StoreCompleted;
            }
            else
            {
                cause = mapEvent instanceof CacheEvent && ((CacheEvent) mapEvent).isSynthetic() ? Cause.Eviction
                        : Cause.Regular;
            }
        }
        else
        {
            cause = Cause.PartitionManagement;
        }

        //now call the abstract event handler with the event and underlying cause
        onBackingMapEvent(new LazyMapEvent(context, mapEvent.getMap(), mapEvent.getId(), mapEvent.getKey(), mapEvent
            .getOldValue(), mapEvent.getNewValue()), cause);
    }

    /**
     * <p>An extension to the standard {@link MapEvent} that will lazily
     * deserialize attributes (if necessary) when first accessed.</p>
     */
    @SuppressWarnings("serial")
    private static class LazyMapEvent extends MapEvent
    {

        /**
         * <p>The {@link BackingMapManagerContext} that should be used
         * to lazily deserialize the event attributes.</p>
         */
        private BackingMapManagerContext backingMapManagerContext;

        /**
         * <p>Standard Constructor.</p>
         * 
         * @param backingMapManagerContext The BackingMapManagerContext associated with this event
         * @param observableMap            The underlying map.
         * @param id                       The event id
         * @param key                      The key that this event was triggered for
         * @param oldValue                 The old value
         * @param newValue                 The new value
         */
        LazyMapEvent(BackingMapManagerContext backingMapManagerContext,
                     ObservableMap observableMap,
                     int id,
                     Object key,
                     Object oldValue,
                     Object newValue)
        {
            super(observableMap, id, key, oldValue, newValue);
            this.backingMapManagerContext = backingMapManagerContext;
        }

        /**
         * {@inheritDoc}
         */
        public Object getKey()
        {
            if (m_oKey instanceof Binary)
            {
                m_oKey = backingMapManagerContext.getKeyFromInternalConverter().convert(m_oKey);
            }

            return m_oKey;
        }

        /**
         * {@inheritDoc}
         */
        public Object getOldValue()
        {
            if (m_oValueOld instanceof Binary)
            {
                m_oValueOld = backingMapManagerContext.getValueFromInternalConverter().convert(m_oValueOld);
            }

            return m_oValueOld;
        }

        /**
         * {@inheritDoc}
         */
        public Object getNewValue()
        {
            if (m_oValueNew instanceof Binary)
            {
                m_oValueNew = backingMapManagerContext.getValueFromInternalConverter().convert(m_oValueNew);
            }

            return m_oValueNew;
        }

        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return String.format("LazyEventMap{%s}", super.toString());
        }
    }
}
