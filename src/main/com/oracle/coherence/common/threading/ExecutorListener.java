/*
 * File: ExecutorListener.java
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

package com.oracle.coherence.common.threading;


/**
 * <p>A {@link ExecutorListener} is an interface to get notified about the execution of a {@link Runnable}. </p>
 *
 * @author Christer Fahlgren
 */
public interface ExecutorListener
{
    /**
     * Just before execution starts the beforeExecute method is called.
     * 
     * @param runnable the {@link Runnable} that is about to be executed.
     */
    
    public void beforeExecute(Runnable runnable);
 
    /**
     * Just after execution stopped, the afterExecute method is called.
     * 
     * @param runnable the {@link Runnable} that was just executed.
     * @param t if the Runnable threw an Exception, t contains the exception - null otherwise
     */
    public void afterExecute(Runnable runnable, Throwable t);

}
