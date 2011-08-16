/*
 * File: NamedCacheObjectProxy.java
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
package com.oracle.coherence.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.processors.InvokeMethodProcessor;
import com.tangosol.net.NamedCache;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

/**
 * The {@link NamedCacheObjectProxy} implements the Java Dynamic Proxy
 * pattern for objects residing in a {@link NamedCache}.
 * 
 * @param <InterfaceType> is the type of the interface the proxy implements
 * 
 * @author Christer Fahlgren 2009.10.30
 *
 */
class NamedCacheObjectProxy<InterfaceType> implements InvocationHandler, MapListener
{

    /**
     * The key for the object.
     */
    private Object key;

    /**
     * The {@link NamedCache} where the object resides. 
     */
    private NamedCache mCache;

    /**
     * The list of registered callbacks.
     */
    private List<ObjectChangeCallback<InterfaceType>> callbacks;

    /**
     * The {@link Logger} to use.
     */
    private static Logger logger = Logger.getLogger(NamedCacheObjectProxy.class.getName());


    /**
     * Standard constructor.
     * 
     * @param key   the key to the object.
     * @param cache the {@link NamedCache} where the object resides.
     */
    public NamedCacheObjectProxy(Object key,
                                 NamedCache cache)
    {
        this.key = key;
        this.mCache = cache;
    }


    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable
    {
        if (method.getName() != "toString")
        {
            if (logger.isLoggable(Level.FINEST))
            {
                logger.log(Level.FINEST, "Calling method {0} for object with key {1} on cache {2}", new Object[] {
                        method.getName(), key, mCache.getCacheName() });
            }
            InvokeMethodProcessor processor = new InvokeMethodProcessor(method.getName(), args);
            Object value = mCache.invoke(key, processor);
            if (value instanceof Throwable)
            {
                throw (Throwable) value;
            }
            return value;
        }
        else
        {
            return (Object) toString();
        }

    }


    /**
     * Register an {@link ObjectChangeCallback} with this {@link NamedCacheObjectProxy}.
     * 
     * @param callback the {@link ObjectChangeCallback} to register
     */
    public void registerChangeCallback(ObjectChangeCallback<InterfaceType> callback)
    {
        if (callbacks == null)
        {
            callbacks = new ArrayList<ObjectChangeCallback<InterfaceType>>();
        }
        synchronized (callbacks)
        {
            if (callbacks.isEmpty())
            {
                mCache.addMapListener(this, key, false);
            }
            callbacks.add(callback);
        }
    }


    /**
     * UnRegister an {@link ObjectChangeCallback} with this {@link NamedCacheObjectProxy}.
     * 
     * @param callback the {@link ObjectChangeCallback} to unregister
     */
    public void unregisterChangeCallback(ObjectChangeCallback<InterfaceType> callback)
    {

        if (callbacks == null)
        {
            callbacks = new ArrayList<ObjectChangeCallback<InterfaceType>>();
        }
        synchronized (callbacks)
        {
            callbacks.remove(callback);
            if (callbacks.isEmpty())
            {
                mCache.removeMapListener(this);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "NamedCacheObjectProxy {" + key + "," + mCache.getCacheName() + "}";
    }


    /**
     * {@inheritDoc}
     */
    public void entryDeleted(MapEvent mapEvent)
    {
        if (callbacks != null)
        {
            synchronized (callbacks)
            {
                for (Iterator<ObjectChangeCallback<InterfaceType>> iter = callbacks.iterator(); iter.hasNext();)
                {
                    iter.next().objectDeleted(mapEvent.getKey());
                }
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void entryInserted(MapEvent mapEvent)
    {
        if (callbacks != null)
        {
            synchronized (callbacks)
            {
                for (Iterator<ObjectChangeCallback<InterfaceType>> iter = callbacks.iterator(); iter.hasNext();)
                {
                    iter.next().objectCreated((InterfaceType) mapEvent.getNewValue());
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void entryUpdated(MapEvent mapEvent)
    {
        if (callbacks != null)
        {
            synchronized (callbacks)
            {

                for (Iterator<ObjectChangeCallback<InterfaceType>> iter = callbacks.iterator(); iter.hasNext();)
                {
                    iter.next().objectChanged((InterfaceType) mapEvent.getNewValue());
                }
            }
        }
    }
}
