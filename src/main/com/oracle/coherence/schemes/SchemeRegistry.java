/*
 * File: SchemeRegistry.java
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
package com.oracle.coherence.schemes;

import java.util.LinkedHashMap;

import com.oracle.coherence.environment.Environment;

/**
 * <p>A {@link SchemeRegistry} is responsible for managing the known set of {@link Scheme}s
 * in an {@link Environment}.</p>
 *
 * @author Brian Oliver
 */
public class SchemeRegistry
{

    /**
     * <p>The {@link Scheme}s currently known by the {@link SchemeRegistry}.</p>
     * 
     * <p>NOTE: This is a {@link LinkedHashMap} as order of registration of {@link Scheme}s is often important
     * to Coherence.</p>
     */
    private LinkedHashMap<String, Scheme> schemes;


    /**
     * <p>Standard Constructor.</p>
     */
    public SchemeRegistry()
    {
        this.schemes = new LinkedHashMap<String, Scheme>();
    }


    /**
     * <p>Registers the specified {@link Scheme} with the specified identity.</p>
     * 
     * <p>NOTE: If a {@link Scheme} with the same identity is already registered, a call
     * to this method will silently override the existing registration.</p>
     * 
     * @param id The identity for the {@link Scheme}.  This identity may be used later to retrieve the {@link Scheme}.
     * @param scheme The {@link Scheme} to register.
     */
    public void registerScheme(String id,
                               Scheme scheme)
    {
        schemes.put(id, scheme);
    }


    /**
     * <p>Requests a reference to the {@link Scheme} that was previously registered with
     * the specified identity.</p>
     * 
     * <p>NOTE: Returns <code>null</code> if the {@link Scheme} requested has not be registered/is
     * unknown.</p>
     * 
     * @param id The identity of the {@link Scheme} being requested.
     * @return A {@link Scheme} or <code>null</code> if not found.
     */
    public Scheme getScheme(String id)
    {
        return schemes.get(id);
    }
}
