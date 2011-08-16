/*
 * File: ResourceResolver.java
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
 * <p>A {@link ResourceResolver} is responsible for resolving and returning objects (often called known as resources) 
 * when requested by {@link Environment}s. For example, the following call</p>
 *
 * <code>
 *    MyResourceType myResource = environment.getResource(MyResourceType.class, param1, param2, param3);
 * </code>
 *
 * <p>Would result in the {@link Environment} implementation calling it's {@link ResourceResolver}s to return an
 * appropriate resource for the provided parameters.</p>
 * 
 * <p>NOTE: {@link Environment} implementations do not cache resources resolved through the use of 
 * {@link ResourceResolver}s. If caching of resolved resources is required, that behavior should be implemented
 * by the {@link ResourceResolver}s themselves.</p>
 * 
 * @param <R> The type of resources that will be resolved and returned by the {@link ResourceResolver}.
 * 
 * @author Brian Oliver
 */
public interface ResourceResolver<R>
{
    /**
     * <p>Attempts to resolve and return a resource using the provided parameters. Must
     * return <code>null</code> if the resource can't be resolved.</p>
     * 
     * @param params Parameters that may be used to resolve the resource.  The meaning of said parameters is
     *               dependent on the {@link ResourceResolver} implementation. 
     * 
     * @return A resource of type <R> or <code>null</code> if one could not be located.
     */
    public R resolveResource(Object... params);
}
