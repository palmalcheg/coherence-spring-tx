/*
 * File: EventDispatcher
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
package com.oracle.coherence.common.events.dispatching;

import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.processing.EventProcessor;
import com.oracle.coherence.environment.Environment;
import com.tangosol.util.Filter;

/**
 * <p>An {@link EventDispatcher} is responsible dispatching {@link Event}s to {@link EventProcessor}s for processing.</p>
 * 
 * <p><strong>IMPORTANT:</strong> In general, {@link Event} dispatching and processing of said {@link Event}s via an 
 * {@link EventDispatcher} always occurs on the thread that requested the dispatch.  This means that should a 
 * Coherence owned thread request an {@link Event} to be dispatched and thus processed, said processing will occur
 * using/on the Coherence owned thread (unless the {@link EventProcessor} is asynchronous or {@link #dispatchEventLater(Event)}
 * was used for dispatching).</p>
 * 
 * <p>In such situations, where a Coherence owned thread performs the dispatching and thus execution of 
 * {@link EventProcessor}s, care should be taken to ensure said {@link EventProcessor}s don't make re-entrant calls onto
 * Coherence APIs, specifically onto the same Coherence Service that owns the said thread.  This typically means
 * avoiding calls to CacheFactory.getCache(...) for caches managed by the said serivce thread.</p>
 * 
 * <p>Should an {@link EventProcessor} require access to a cache, and it can't be guaranteed that is safe to do so, the
 * {@link EventProcessor} should be made "asynchronous".</p> 
 *  
 * @author Brian Oliver
 */
public interface EventDispatcher
{

    /**
     * <p>Returns the {@link Environment} in which the {@link EventDispatcher} is operating.</p>
     * 
     * @return An {@link Environment}.
     */
    public Environment getEnvironment();


    /**
     * <p>Dispatches the specified {@link Event} to be processed by the specified and other appropriate 
     * {@link EventProcessor}s <strong>using the calling thread.</strong>.</p>
     * 
     * @param <E> The type of {@link Event}
     * @param event The {@link Event} to be processed by the specified {@link EventProcessor}.
     * @param eventProcessor The {@link EventProcessor} to process the specified {@link Event}
     */
    public <E extends Event> void dispatchEvent(E event,
                                                EventProcessor<E> eventProcessor);


    /**
     * <p>Dispatches the specified {@link Event} to be processed by the appropriate {@link EventProcessor}s
     * <strong>using the calling thread.</strong>.</p>
     * 
     * @param <E>   The type of {@link Event}
     * @param event The {@link Event} to be processed by the {@link EventProcessor}s.
     */
    public <E extends Event> void dispatchEvent(E event);


    /**
     * <p>Requests that the specified {@link Event} to be dispatched and processed asynchronously
     * <strong>using an alternative thread (not the calling thread)</strong>.</p>
     * 
     * <p><strong>NOTE:</strong> Use of this method is <strong>strongly discouraged</strong>. It is reserved only for use
     * in circumstances where Coherence events <strong>must always</strong> be processed on a non-Coherence thread. 
     * There is no guarantee as to when the specified {@link Event} will be dispatched and processed, only that it
     * will occur at sometime in the future.</p>
     * 
     * @param <E>   The type of {@link Event}
     * @param event The {@link Event} to be processed by the {@link EventProcessor}s.
     */
    public <E extends Event> void dispatchEventLater(E event);


    /**
     * <p>Registers the specified {@link EventProcessor} and associated {@link Filter} to be 
     * used to select and process {@link Event}s dispatched by the {@link #dispatchEvent(Event)} 
     * method.</p>
     * 
     * @param <E> The type of {@link Event}
     * @param filter The {@link Filter} to use to filter {@link Event}s prior to being executed
     *               by the {@link EventProcessor}.
     *                 
     *               NOTE: {@link Filter}s must implement hashCode() and equals(Object) methods correctly.
     * @param eventProcessor The {@link EventProcessor} to register.
     */
    public <E extends Event> void registerEventProcessor(Filter filter,
                                                         EventProcessor<E> eventProcessor);


    /**
     * <p>Unregisters the specified {@link EventProcessor} and associated {@link Filter} for processing
     * {@link Event}s.</p>
     * 
     * @param <E> The type of {@link Event}
     * @param filter The {@link Filter} use to register the {@link EventProcessor}.
     *               NOTE: {@link Filter}s must implement hashCode() and equals(Object) methods correctly
     * @param eventProcessor The {@link EventProcessor} that was registered.
     */
    public <E extends Event> void unregisterEventProcessor(Filter filter,
                                                           EventProcessor<E> eventProcessor);
}
