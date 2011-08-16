/*
 * File: BackingMapEntryUpdatedEvent.java
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
package com.oracle.coherence.common.events.backingmap;

import java.util.Map.Entry;

import com.oracle.coherence.common.events.EntryUpdatedEvent;
import com.tangosol.net.BackingMapManagerContext;

/**
 * <p>An implementation of {@link EntryUpdatedEvent} with {@link BackingMapEntryEvent} support.</p>
 * 
 * @author Brian Oliver
 */
public class BackingMapEntryUpdatedEvent extends AbstractBackingMapEntryEvent implements EntryUpdatedEvent
{
    /**
     * <p>The original {@link Entry} before it was updated.</p>
     */
    private EventEntry originalEntry;

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param backingMapManagerContext The BackingMapManagerContext associated with this event
     * @param cacheName                The name of the cache where this event was triggered
     * @param key                      The key associated with this event
     * @param originalValue            The original value for this entry
     * @param newValue                 The new value for this entry
     */
    public BackingMapEntryUpdatedEvent(BackingMapManagerContext backingMapManagerContext,
                                       String cacheName,
                                       Object key,
                                       Object originalValue,
                                       Object newValue)
    {
        super(backingMapManagerContext, cacheName, key, newValue);
        this.originalEntry = new EventEntry(key, originalValue);
    }

    /**
     * {@inheritDoc}
     */
    public Entry<?, ?> getOriginalEntry()
    {
        return originalEntry;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("%s{cacheName=%s, originalEntry=%s, newEntry=%s}", getClass().getName(), getCacheName(),
            getOriginalEntry(), getEntry());
    }
}
