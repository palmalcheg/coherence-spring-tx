/*
 * File: BackingMapEntryEvictedEvent.java
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

import com.tangosol.net.BackingMapManagerContext;

/**
 * <p>An {@link BackingMapEntryEvictedEvent} is a specialized {@link BackingMapEntryRemovedEvent} that
 * represents when an {@link java.util.Map.Entry} been evicted from a {@link com.tangosol.net.NamedCache}.</p> 
 *  
 * @author Brian Oliver
 */
public class BackingMapEntryEvictedEvent extends BackingMapEntryRemovedEvent
{

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param backingMapManagerContext The BackingMapManagerContext associated with this event
     * @param cacheName                The name of the cache where this event was triggered
     * @param key                      The key associated with this event
     * @param value                    The value associated with this event
     */
    public BackingMapEntryEvictedEvent(BackingMapManagerContext backingMapManagerContext,
                                       String cacheName,
                                       Object key,
                                       Object value)
    {
        super(backingMapManagerContext, cacheName, key, value);
    }
}
