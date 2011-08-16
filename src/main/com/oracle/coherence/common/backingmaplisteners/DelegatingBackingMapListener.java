/*
 * File: DelegatingBackingMapListener.java
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
package com.oracle.coherence.common.backingmaplisteners;

import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.MapEvent;

/**
 * <p>A {@link DelegatingBackingMapListener} will delegate backing map events to
 * the object on which the event occurred (assuming the said object 
 * implements {@link LifecycleAwareCacheEntry}).</p>
 * 
 * <p>For {@link MapEvent#ENTRY_INSERTED} events, the event will be delegated to the 
 * {@link MapEvent#getNewValue()}.</p>
 * 
 * <p>For {@link MapEvent#ENTRY_UPDATED} events, the event will be delegated to the 
 * {@link MapEvent#getNewValue()}.</p>
 *  
 * <p>For {@link MapEvent#ENTRY_DELETED} events, the event will be delegated to the 
 * {@link MapEvent#getOldValue()}.</p>
 * 
 * @deprecated Use {@link com.oracle.coherence.common.events.dispatching.listeners.DelegatingBackingMapListener} instead.
 * 
 * @author Brian Oliver
 */
@Deprecated
public class DelegatingBackingMapListener extends AbstractMultiplexingBackingMapListener
{

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param backingMapManagerContext The BackingMapManagerContext associated with this listener
     */
    public DelegatingBackingMapListener(BackingMapManagerContext backingMapManagerContext)
    {
        super(backingMapManagerContext);
    }

    /**
     * {@inheritDoc}
     */
    public void onBackingMapEvent(MapEvent mapEvent,
                                  Cause cause)
    {
        if ((mapEvent.getId() == MapEvent.ENTRY_INSERTED || mapEvent.getId() == MapEvent.ENTRY_UPDATED)
                && mapEvent.getNewValue() instanceof LifecycleAwareCacheEntry)
        {

            ((LifecycleAwareCacheEntry) mapEvent.getNewValue()).onCacheEntryLifecycleEvent(mapEvent, cause);

        }
        else if (mapEvent.getId() == MapEvent.ENTRY_DELETED
                && mapEvent.getOldValue() instanceof LifecycleAwareCacheEntry)
        {

            ((LifecycleAwareCacheEntry) mapEvent.getOldValue()).onCacheEntryLifecycleEvent(mapEvent, cause);
        }
    }
}
