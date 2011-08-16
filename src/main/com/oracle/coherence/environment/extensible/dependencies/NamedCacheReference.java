/*
 * File: NamedCacheReference.java
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
package com.oracle.coherence.environment.extensible.dependencies;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Service;

/**
 * <p>An implementation of a {@link DependencyReference} for a  
 * Coherence{@link NamedCache}s.</p>
 * 
 * @author Brian Oliver
 */
public class NamedCacheReference extends AbstractNamedDependencyReference
{
    /**
     * <p>Standard Constructor.</p>
     * 
     * @param cacheName The name of the {@link NamedCache} to reference.
     */
    public NamedCacheReference(String cacheName)
    {
        super(cacheName);
    }


    /**
     * <p>Standard Constructor (for use with {@link NamedCache}s).</p>
     * 
     * @param namedCache The {@link Service} for which to create a reference.
     */
    public NamedCacheReference(NamedCache namedCache)
    {
        this(namedCache.getCacheName());
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReferencing(Object object)
    {
        //if the object is a service that the cache uses, we're good!
        return object != null
                && object instanceof Service
                && CacheFactory.getCache(getName()).getCacheService().getInfo().getServiceName().equals(
                    ((Service) object).getInfo().getServiceName());
    }
}
