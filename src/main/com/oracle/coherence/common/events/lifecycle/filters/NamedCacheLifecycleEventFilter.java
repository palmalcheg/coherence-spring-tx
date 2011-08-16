/*
 * File: NamedCacheLifecycleEventFilter.java
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
package com.oracle.coherence.common.events.lifecycle.filters;

import com.oracle.coherence.common.events.lifecycle.NamedCacheLifecycleEvent;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;

/**
 * <p>A {@link NamedCacheLifecycleEventFilter} may be used to filter {@link NamedCacheLifecycleEvent}s
 * for a specifically named {@link NamedCache}s.</p>
 * 
 * <p>NOTE 1: {@link NamedCacheLifecycleEventFilter}s are not serializable as they are only ever used locally.</p>
 * 
 * <p>NOTE 2: You may use Coherence cache name mapping wildcards for the specified named cache.</p>
 *
 * @author Brian Oliver
 */
public class NamedCacheLifecycleEventFilter implements Filter
{

    /**
     * <p>The name of the {@link NamedCache} we're filtering.  May be a Coherence Cache Name wildcard.</p>
     */
    private String cacheName;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param cacheName The name of the cache we're looking for (may contain wildcards)  
     */
    public NamedCacheLifecycleEventFilter(String cacheName)
    {
        this.cacheName = cacheName.trim();
    }


    /**
     * {@inheritDoc}
     */
    public boolean evaluate(Object object)
    {
        if (object instanceof NamedCacheLifecycleEvent)
        {
            NamedCacheLifecycleEvent event = ((NamedCacheLifecycleEvent) object);

            if (cacheName.equals("*"))
            {   
                //a * wildcard matches any cache name
                return true;
            }
            else if (cacheName.equals(event.getCacheName()))
            {
                //there's an exact match
                return true;
            }
            else
            {
                int pos = cacheName.indexOf("*");
                if (pos >= 0)
                {
                    //does the prefix (up to the wildcard) match exactly with the start of the cache name
                    String prefix = cacheName.substring(0, pos);
                    return event.getCacheName().startsWith(prefix);
                }
                else
                {
                    //no wildcard and thus no match
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
