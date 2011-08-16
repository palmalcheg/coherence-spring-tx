/*
 * File: AsynchronousEventProcessorAdapter.java
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
package com.oracle.coherence.common.events.processing;

import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.dispatching.EventDispatcher;

/**
 * <p>An {@link AsynchronousEventProcessorAdapter} adapts another {@link EventProcessor} implementation
 * to ensure that it is processed asynchronously (on another thread other than the one that raise the said
 * {@link Event}),</p>
 *
 * @author Brian Oliver
 */
public class AsynchronousEventProcessorAdapter<E extends Event> extends AbstractAsynchronousEventProcessor<E>
{
    /**
     * <p>The {@link EventProcessor} being adapted.</p>
     */
    private EventProcessor<E> eventProcessor;
    
    
    /**
     * <p>Standard Constructor.</p>
     * 
     * @param eventProcessor The {@link EventProcessor} to adapt.
     */
    public AsynchronousEventProcessorAdapter(EventProcessor<E> eventProcessor)
    {
        this.eventProcessor = eventProcessor;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void processLater(EventDispatcher eventDispatcher,
                             E event)
    {
        eventProcessor.process(eventDispatcher, event);
    }
}
