/*
 * File: Environment.java
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
package com.oracle.coherence.environment;

/**
 * <p>An {@link Environment} provides access to strongly typed resources and contextual information for one or 
 * more applications, components and/or extensions <strong>within</strong> a Coherence-based process (ie: JVM).</p>
 * 
 * <p>In most cases the {@link com.tangosol.net.ConfigurableCacheFactory} implementations are responsible for implementing
 * the {@link Environment}.  Consequently the typical approach to locate and access a configured {@link Environment} 
 * is to simply perform the following;</p>
 * 
 * <code>({@link Environment}){@link com.tangosol.net.CacheFactory#getConfigurableCacheFactory()}</code>
 * 
 * @author Brian Oliver
 */
public interface Environment
{
    /**
     * <p>Attempts to resolve and return a resource with the specified interface using the provided parameters. Returns
     * <code>null</code> if the resource can't be resolved.</p>
     * 
     * @param <R> The type of the expected resource
     * 
     * @param clazz The interface {@link Class} of the expected resource
     * @param params Parameters that may be used to resolve the resource.  The meaning of said parameters is
     *               dependent on the {@link ResourceResolver} implementation found to resolve the resource.
     * 
     * @return A resource of type <R> or <code>null</code> if one could not resolved.
     */
    public <R extends Object> R getResource(Class<R> clazz,
                                            Object... params);


    /**
     * <p>Registers a resource with the {@link Environment} that may be later retrieved using the {@link #getResource(Class, Object...)}
     * method using the specified {@link Class} (but without parameters).</p>
     * 
     * @param <R> The type of the interface to be associated with the resource
     * @param clazz The interface {@link Class} of the resource to register
     * @param resource The resource to register
     */
    public <R> void registerResource(Class<R> clazz,
                                     Object resource);


    /**
     * <p>Registers a {@link ResourceResolver} with the {@link Environment} so that it may resolve resources of 
     * type <R> when requested using the {@link #getResource(Class, Object...)} method.</p>
     * 
     * <p>NOTE: A single {@link ResourceResolver} may be registered for multiple types.</p>
     * 
     * @param <R> The type of the resource to be resolved.
     * @param clazz The class instance of the type of resource.
     * @param resourceResolver The {@link ResourceResolver}.
     */
    public <R> void registerResourceResolver(Class<R> clazz,
                                             ResourceResolver<R> resourceResolver);


    /**
     * <p>Returns the {@link ClassLoader} that should be used to load any classes used by the 
     * {@link Environment}</p>
     *  
     * @return The {@link ClassLoader} for the {@link Environment}.
     */
    public ClassLoader getClassLoader();
}
