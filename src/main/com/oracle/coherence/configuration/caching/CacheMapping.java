/*
 * File: CacheMapping.java
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.oracle.coherence.configuration.parameters.Parameter;
import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.configuration.parameters.SystemPropertyParameterScope;

/**
 * A {@link CacheMapping} represents the configuration information for an individual mapping from a named cache
 * to a cache scheme, including any specific {@link Parameter}s (represented as a {@link ParameterScope})
 * for the said mapping.
 *
 * @author Brian Oliver
 */
public class CacheMapping
{

    /**
     * The name of the cache.
     */
    private String cacheName;

    /**
     * The name of the  scheme to which the cache is mapped.
     */
    private String schemeName;

    /**
     * The {@link ParameterScope} containing the {@link Parameter}s defined by the {@link CacheMapping}.
     */
    private ParameterScope parameterScope;

    /**
     * Each {@link CacheMapping} may contain a set of named decorations by type.
     */
    private HashMap<Class<?>, HashMap<?, ?>> decorations;


    /** 
     * Standard Constructor.
     * 
     * @param cacheName The name of the cache
     * @param schemeName The scheme for the cache
     * @param parameterScope The {@link ParameterScope} containing the {@link Parameter}s defined for the mapping.
     */
    public CacheMapping(String cacheName,
                        String schemeName,
                        ParameterScope parameterScope)
    {
        this.cacheName = cacheName.trim();
        this.schemeName = schemeName.trim();
        this.parameterScope = parameterScope == null ? SystemPropertyParameterScope.INSTANCE : parameterScope;
        this.decorations = new HashMap<Class<?>, HashMap<?, ?>>();
    }


    /**
     * Returns the name of the cache for the {@link CacheMapping}.
     * 
     * @return A String representing the cache name for the {@link CacheMapping}
     */
    public String getCacheName()
    {
        return cacheName;
    }

    
    /**
     * Determines if the {@link CacheMapping} is for (matches) the specified cache name.
     * 
     * @param cacheName The cacheName to check for a match
     * @return <code>true</code> if the {@link CacheMapping} is for the specified cache name, 
     *         <code>false</code> otherwise.
     */
    public boolean isForCacheName(String cacheName)
    {
        if (getCacheName().equals("*"))
        {
            return true;
        }
        else if (getCacheName().contains("*"))
        {
            String pattern = getCacheName().substring(0, getCacheName().indexOf("*"));
            return cacheName.startsWith(pattern);
        }
        else
        {
            return false;
        }
    }
    

    /**
     * Returns the scheme name for the {@link CacheMapping}.
     * 
     * @return A String representing the scheme name for the {@link CacheMapping}
     */
    public String getSchemeName()
    {
        return schemeName;
    }


    /**
     * Returns the {@link ParameterScope} that contains the {@link Parameter}s 
     * defined for the {@link CacheMapping}.
     * 
     * @return A {@link ParameterScope}
     */
    public ParameterScope getParameterScope()
    {
        return parameterScope;
    }


    /**
     * Returns if the {@link CacheMapping} has the specified decoration.
     * 
     * @param type The type of the decoration
     * @param decorationKey The key of the decoration
     * @return <code>true</code> if the named decoration exists on the {@link CacheMapping}, 
     *         <code>false</code> otherwise.
     */
    public <T, N> boolean hasDecoration(Class<T> type,
                                        N decorationKey)
    {
        return getDecoration(type, decorationKey) != null;
    }


    /**
     * Returns the decoration associated with the specified name.
     *
     * @param <T>
     * @param type The type of the decoration
     * @param decorationKey The name of the decoration
     * @return The decoration or <code>null</code> if the decoration is not defined.
     */
    @SuppressWarnings("unchecked")
    public <T, N> T getDecoration(Class<T> type,
                                  N decorationKey)
    {
        return (T) (decorations.containsKey(type) ? decorations.get(type).get(decorationKey) : null);
    }


    /**
     * Returns an {@link Iterator} over the decorations for the specified type.
     * 
     * @param <T>
     * @param type The type of the decorations required.
     * @return An {@link Iterator} over the decorates of the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T> Iterator<T> getDecorations(Class<T> type)
    {
        return (decorations.containsKey(type) ? decorations.get(type).values().iterator() : Collections.EMPTY_LIST
            .iterator());
    }


    /**
     * Adds and/or overrides an existing decoration with the specified type and name on the {@link CacheMapping}.
     * 
     * @param <T>
     * @param type The type of the decoration
     * @param decorationKey The name of the decoration (should be unique for the {@link CacheMapping}).
     * @param decoration The decoration to add
     */
    @SuppressWarnings("unchecked")
    public <T, N> void addDecoration(Class<T> type,
                                     N decorationKey,
                                     T decoration)
    {
        HashMap<N, T> namedDecorations = (HashMap<N, T>) decorations.get(type);
        if (namedDecorations == null)
        {
            namedDecorations = new HashMap<N, T>();
            decorations.put(type, namedDecorations);
        }

        namedDecorations.put(decorationKey, decoration);
    }
    
    
    /**
     * Adds all of the decorations from the provided {@link CacheMapping} to the this {@link CacheMapping}.
     * 
     * @param cacheMapping The {@link CacheMapping} from which to retrieve decorations.
     */
    @SuppressWarnings("unchecked")
    public void addDecorationsFrom(CacheMapping cacheMapping)
    {
        for(Class<?> type : cacheMapping.decorations.keySet())
        {
            for(Object decorationKey : cacheMapping.decorations.get(type).keySet())
            {
                addDecoration((Class<Object>)type, (Object)decorationKey, (Object)cacheMapping.decorations.get(type).get(decorationKey));
            }
        }
    }
}