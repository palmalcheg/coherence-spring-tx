/*
 * File: NamedCacheLifecycleEvent.java
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
 * <p>The {@link NamedCacheLifecycleEvent} represents {@link LifecycleEvent}s, 
 * specifically for named caches.</p>
 * 
 * @author Brian Oliver
 */
public abstract class NamedCacheLifecycleEvent extends AbstractLifecycleEvent<String>
{

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param cacheName The name of the cache for the {@link NamedCacheLifecycleEvent}.
     */
    public NamedCacheLifecycleEvent(String cacheName)
    {
        super(cacheName);
    }


    /**
     * <p>Returns the name of the cache about which this Event concerned.</p>
     * 
     * @return The name of the cache
     */
    public String getCacheName()
    {
        return getSource();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("%s{cacheName=%s}", this.getClass().getName(), getCacheName());
    }
}
