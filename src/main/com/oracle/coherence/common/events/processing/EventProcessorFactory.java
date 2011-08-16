/*
 * File: EventProcessorFactory.java
 * 
 * Copyright (c) 2009. All Rights Reserved. Oracle Corporation.
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

/**
 * <p>An {@link EventProcessorFactory} produces {@link EventProcessor}s that are capable of 
 * processing specified {@link Event}s.</p>
 * 
 * @param <E> The {@link Event} tpe
 *  
 * @author Brian Oliver
 */
public interface EventProcessorFactory<E extends Event>
{

    /**
     * <p>Returns an {@link EventProcessor} that is capable of processing the specified {@link Event}.
     * 
     * @param event the Event to produce an {@link EventProcessor} for
     * 
     * @return an {@link EventProcessor} that is capable of processing the specified {@link Event}
     */
    public EventProcessor<E> getEventProcessor(E event);
}
