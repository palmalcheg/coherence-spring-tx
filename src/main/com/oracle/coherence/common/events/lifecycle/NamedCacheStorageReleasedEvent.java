/*
 * File: NamedCacheStorageReleasedEvent.java
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
package com.oracle.coherence.common.events.lifecycle;

/**
 * <p>The {@link NamedCacheStorageReleasedEvent} represents that the storage, typically a backing map,
 * has been released and is no longer able to be used.</p>
 * 
 * <p><strong>NOTE: This event will be raised on every member in the cluster that provides storage for the 
 * specified named cache.</p>
 * 
 * <p>This events mean that the specified named cache has just been destroyed or that the infrastructure locally
 * has been release.  The ultimate meaning is dependent on the scheme of the cache.</p>
 * 
 * @author Brian Oliver
 */
public class NamedCacheStorageReleasedEvent extends NamedCacheLifecycleEvent
{

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param cacheName The name of the cache for the {@link NamedCacheStorageReleasedEvent}.
     */
    public NamedCacheStorageReleasedEvent(String cacheName)
    {
        super(cacheName);
    }
}
