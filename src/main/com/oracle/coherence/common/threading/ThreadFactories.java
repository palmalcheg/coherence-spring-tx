/*
 * File: ThreadFactories.java
 * 
 * Copyright (c) 2008. All Rights Reserved. Oracle Corporation.
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

import java.util.concurrent.ThreadFactory;

/**
 * <p>The {@link ThreadFactories} class provides a simple mechanism to
 * provide parameterized {@link ThreadFactory}s.</p>
 * 
 * @author Brian Oliver
 */
public class ThreadFactories
{
    /**
     * <p>A helper to create a {@link ThreadFactory} based on the following parameters.</p>
     *  
     * @param isDaemon Should the {@link ThreadFactory} produce daemon threads.
     * @param threadName The threadName of the produced threads.
     * @param threadGroup The threadGroup for the produced threads.
     * 
     * @return A {@link ThreadFactory}
     */
    public static ThreadFactory newThreadFactory(final boolean isDaemon,
                                                 final String threadName,
                                                 final ThreadGroup threadGroup)
    {
        return new ThreadFactory()
        {
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(threadGroup, r);
                thread.setDaemon(isDaemon);
                thread.setName(threadName + ":" + thread.getName());
                return thread;
            }
        };
    }
}
