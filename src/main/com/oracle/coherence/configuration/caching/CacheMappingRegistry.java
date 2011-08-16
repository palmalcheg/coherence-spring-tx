/*
 * File: CacheMappings.java
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
package com.oracle.coherence.configuration.caching;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.environment.Environment;

/**
 * <p>A {@link CacheMappingRegistry} is a registry of known {@link CacheMapping}s.</p>
 *
 * <p>To request the {@link CacheMappingRegistry} instance for an {@link Environment}, use the following;
 * <code>environment.getResource({@link CacheMappingRegistry}.class)</code></p>
 *
 * @author Brian Oliver
 */
public class CacheMappingRegistry
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static final Logger logger = Logger.getLogger(CacheMappingRegistry.class.getName());

    /**
     * <p>The set of {@link CacheMapping}s.</p>
     */
    private LinkedHashMap<String, CacheMapping> cacheMappings;


    /**
     * <p>Standard Constructor.</p>
     */
    public CacheMappingRegistry()
    {
        this.cacheMappings = new LinkedHashMap<String, CacheMapping>();
    }


    /**
     * <p>Adds and/or Overrides the {@link CacheMapping} for a specific named cache in the {@link CacheMappingRegistry}.</p>
     * 
     * @param cacheMapping The {@link CacheMapping} to add/override
     * 
     * @return <code>true</code> When the {@link CacheMapping} was added successfully, <code>false</code> if it was not added.
     */
    public boolean addCacheMapping(CacheMapping cacheMapping)
    {
        //ensure that an existing mapping with exactly the same name has not already been defined.
        CacheMapping existingCacheMapping = cacheMappings.get(cacheMapping.getCacheName());
        if (existingCacheMapping != null)
        {
            logger.log(Level.CONFIG, "WARNING: The cache mapping for the <cache-name>{0}</cache-name> has been duplicated/overridden."
                + "The new cache mapping declaration for <cache-name>{0}</cache-name> will be ignored, but decorations will be retained and merged.", cacheMapping.getCacheName());
            
            existingCacheMapping.addDecorationsFrom(cacheMapping);
            
            return false;
        }
        else
        {
            //warn if the new cache mapping matches an existing definition
            existingCacheMapping = findCacheMapping(cacheMapping.getCacheName());
            if (existingCacheMapping != null)
            {
                logger.log(Level.CONFIG, "WARNING: The cache mapping for the <cache-name>{0}</cache-name> matches an existing declaration called <cache-name>{1}</cache-name>."
                    + "While the cache mapping for <cache-name>{0}</cache-name> will be accepted, it may never be used due to the existing overriding declaration of <cache-name>{1}</cache-name>.", new Object[] {cacheMapping.getCacheName(), existingCacheMapping.getCacheName()});
            }
            
            //add the cache mapping
            cacheMappings.put(cacheMapping.getCacheName(), cacheMapping);
        
            return true;
        }
    }


    /**
     * <p>Attempts to find the {@link CacheMapping} that matches the specified cache name.  The matching algorithm 
     * first attempts to find an exact match of a {@link CacheMapping} with the provided cache name.  Should that fail, 
     * all of the currently registered wild-carded {@link CacheMapping}s are searched to find a match (in the order
     * in which they were registered).</p>
     * 
     * @param cacheName The cache name
     * 
     * @return <code>null</code> if a {@link CacheMapping} could not be located for the specified cache name.
     */
    public CacheMapping findCacheMapping(String cacheName)
    {
        CacheMapping cacheMapping;
        
        //is there an exact match for the provided cacheName?  
        //ie: is the cacheName explicitly defined as a CacheMapping without wildcards?
        if (cacheMappings.containsKey(cacheName))
        {
            cacheMapping = cacheMappings.get(cacheName);
        }
        else
        {
            //attempt to find the most specific (ie: longest) wildcard defined CacheMapping that matches the cacheName
            cacheMapping = null;
            for (Iterator<CacheMapping> iterator = cacheMappings.values().iterator(); iterator.hasNext();)
            {
                CacheMapping mapping = iterator.next();
                if (mapping.isForCacheName(cacheName))
                {
                    if (cacheMapping == null)
                    {
                        cacheMapping = mapping;
                    }
                    else if (mapping.getCacheName().length() > cacheMapping.getCacheName().length())
                    {
                        cacheMapping = mapping;
                    }
                }
            }
        }

        return cacheMapping;
    }
}
