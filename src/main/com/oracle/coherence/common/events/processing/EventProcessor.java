/*
 * File: EventProcessor.java
 * 
 * Copyright (c) 2009-2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.events.processing;

import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.dispatching.EventDispatcher;

/**
 * <p>An {@link EventProcessor} provides an implementation that is capable of
 * "processing" an {@link Event}.</p>
 * 
 * <p>While it's possible for {@link EventProcessor} instances to be reused,
 * they themselves should be <strong>immutable</strong> or thread-safe so that
 * if an instance is scheduled for execution by multiple threads concurrently,
 * their state is not corrupted.</p>
 * 
 * <p><strong>NOTE:</strong>{@link EventProcessor}s are <strong>always</strong>
 * on non-Coherence threads.</p>
 * 
 * @param <E> The {@link com.oracle.coherence.common.events.Event} type
 * 
 * @author Brian Oliver
 */
public interface EventProcessor<E extends Event>
{

    /**
     * <p>Perform necessary processing of the provided {@link Event}.</p>
     * 
     * @param eventDispatcher
     *            The {@link EventDispatcher} that dispatched the specified
     *            {@link Event} for processing
     * @param event
     *            The {@link Event} to be processed
     */
    public void process(EventDispatcher eventDispatcher,
                        E event);
}
