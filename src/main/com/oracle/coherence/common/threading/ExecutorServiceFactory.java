/*
 * File: ExecutorServiceFactory.java
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * <p>A {@link ExecutorServiceFactory} to create different {@link ExecutorService} variants.</p>
 *
 * @author Christer Fahlgren
 */
public class ExecutorServiceFactory
{

    /**
     * A fixed thread pool ExecutorService.
     * 
     * @param nThreads the number of threads in the pool
     * 
     * @return the {@link ExecutorService}
     */
    public static ExecutorService newFixedThreadPool(int nThreads)
    {
        return new LogExceptionThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    }


    /**
     * A fixed thread pool ExecutorService.
     * 
     * @param nThreads      the number of threads in the pool
     * @param threadFactory the {@link ThreadFactory} to use
     * 
     * @return the {@link ExecutorService}
     */
    public static ExecutorService newFixedThreadPool(int nThreads,
                                                     ThreadFactory threadFactory)
    {
        return new LogExceptionThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), threadFactory);
    }


    /**
     * A single thread {@link ExecutorService}.
     * 
     * @return the {@link ExecutorService}
     */
    public static ExecutorService newSingleThreadExecutor()
    {
        return new LogExceptionThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }


    /**
     * A single thread {@link ExecutorService}.
     * 
     * @param threadFactory the {@link ThreadFactory} to use
     * 
     * @return the {@link ExecutorService}
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory)
    {
        return new LogExceptionThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            threadFactory);
    }


    /**
     * A fixed thread pool ScheduledExecutorService.
     * 
     * @param nThreads the number of threads in the pool
     * 
     * @return the {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService newScheduledThreadPool(int nThreads)
    {
        return new LogExceptionScheduledThreadPoolExecutor(nThreads);
    }


    /**
     * A fixed thread pool ScheduledExecutorService.
     * 
     * @param nThreads      the number of threads in the pool
     * @param threadFactory the {@link ThreadFactory} to use
     * 
     * @return the {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService newScheduledThreadPool(int nThreads,
                                                         ThreadFactory threadFactory)
    {
        return new LogExceptionScheduledThreadPoolExecutor(nThreads, threadFactory);
    }


    /**
     * A single thread {@link ScheduledExecutorService}.
     * 
     * @return the {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor()
    {
        return new LogExceptionScheduledThreadPoolExecutor(1);
    }


    /**
     * A single thread {@link ScheduledExecutorService}.
     * 
     * @param threadFactory the {@link ThreadFactory} to use
     * 
     * @return the {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory)
    {
        return new LogExceptionScheduledThreadPoolExecutor(1, threadFactory);
    }

}
