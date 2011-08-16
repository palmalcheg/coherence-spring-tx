/*
 * File: ChangeIndication.java
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
 * <p>The {@link ChangeIndication} interface is used to indicate whether an object has a change
 * that needs persistence. {@code beforeChange} is called before the potentially state changing operation
 * is called on the object. After the state changing operation, changed() is called to determine
 * whether a change actually happened.</p>
 *
 * @author Christer Fahlgren
 */
public interface ChangeIndication
{

    /**
     * beforeChange is called on the object before the state changing operation was called.
     */
    void beforeChange();


    /**
     * changed is called to determine whether the object changed since the call to beforeChange.
     * 
     * @return true if the object changed
     */
    boolean changed();
}
