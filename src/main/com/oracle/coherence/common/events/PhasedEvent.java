/*
 * File: PhasedEvent.java
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
package com.oracle.coherence.common.events;

/**
 * <p>A {@link PhasedEvent} is a specialized {@link Event} that represents a
 * long running activity which has an explicit starting (commenced) and
 * finishing (completed) {@link Event}.</p>
 * 
 * @author Brian Oliver
 */
public interface PhasedEvent extends Event
{

    /**
     * <p>The valid {@link Phase}s for the {@link PhasedEvent}.</p> 
     */
    public enum Phase
    {
        /**
         * <p>The event has commenced running.</p>
         */
        Commenced, 
        /**
         * <p>The event has completed running.</p>
         */
        Completed
    };

    /**
     * <p>Returns the {@link Phase} of the {@link PhasedEvent}.</p>
     * 
     * @return the {@link Phase} of the {@link PhasedEvent}.
     */
    public Phase getPhase();
}
