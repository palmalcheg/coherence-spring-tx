/*
 * File: Scheme.java
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

/**
 * <p>A {@link Scheme} is responsible for a). capturing declarative configuration information about 
 * a specific type of resource, and b). providing a mechanism to "realize" (construct/resolve/reference)
 * an instance of the said resource type when one is required.</p>
 * 
 * <p>For the purposes of Schemes, a resource is simply an object that represents either; 
 * application state, an object providing a service, a connection, a po(j|c|n)os, a bean, a thread, 
 * a cache, a context or in fact a framework.  No special class or interface is required for
 * an object to be considered to be a resource.</p>
 * 
 * <p>Terminology:</p>
 * <ol>
 *  <li>The term "Scheme Instance" is used to refer to an object that implements the Scheme interface.</li>
 *  <li>The terms "Scheme Realization" and "Realizing a Scheme" refers to the process of creating/resolving 
 *      a resource based on a Scheme Instance.</li> 
 * </ol>
 * 
 * <p>NOTE: The method(s) for realizing resources from {@link Scheme}s are class specific
 * and thus only appear on specializations of this interface.  The typical and customary name 
 * for said methods however is "realize".</p>
 *
 * @author Brian Oliver
 */
public abstract interface Scheme
{
    //this is a marker interface. you should never have an instance of this.
}
