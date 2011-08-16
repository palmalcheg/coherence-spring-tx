/*
 * File: LifecycleAwareCacheEntry.java
 * 
 * Copyright (c) 2009. All Rights Reserved. Oracle Corporation.
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

import com.tangosol.util.MapEvent;

/**
 * <p>A {@link LifecycleAwareCacheEntry} is a cache entry that is capable of handling
 * backing map events that occur on itself. </p>
 * 
 * <p>In order for a {@link LifecycleAwareCacheEntry} to receive events, the underlying
 * cache storing the said {@link LifecycleAwareCacheEntry}s must be configured to use
 * a {@link DelegatingBackingMapListener}.</p>
 * 
 * @deprecated Use {@link com.oracle.coherence.common.events.processing.LifecycleAwareEntry} instead.
 * 
 * @author Brian Oliver
 */
@Deprecated
public interface LifecycleAwareCacheEntry
{

    /**
     * <p>Implement this method to handle life-cycle events on a cache entry
     * (issued by a {@link DelegatingBackingMapListener}).</p>
     * 
     * @param mapEvent A standard mapEvent
     * @param cause The underlying cause of the event
     */
    public void onCacheEntryLifecycleEvent(MapEvent mapEvent,
                                           Cause cause);

}
