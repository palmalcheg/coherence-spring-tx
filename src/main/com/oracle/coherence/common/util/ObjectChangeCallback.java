/*
 * File: ObjectChangeCallback.java
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

/**
 * <p>An {@link ObjectChangeCallback} is a callback interface to be used to be informed about changes
 * to remote objects represented by a {@link NamedCacheObjectProxy}. </p>
 *
 * @param <T> the type of the object that we are calling back for
 * 
 * @author Christer Fahlgren
 */
public interface ObjectChangeCallback<T>
{

    /**
     * Callback when the remote object has changed. A local copy of the changed object is passed as the parameter.
     * 
     * @param object the changed object
     */
    public void objectChanged(T object);


    /**
     * Callback when the remote object has been created. A local copy of the created object is passed as the parameter.
     * 
     * @param object the created object
     */
    public void objectCreated(T object);


    /**
     * Callback when the remote object has been deleted. The key to the remote object is passed as a parameter.
     * 
     * @param key the key to the remote object
     */
    public void objectDeleted(Object key);

}
