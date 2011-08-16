/*
 * File: LifecycleEvent.java
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
package com.oracle.coherence.common.events.lifecycle;

import com.oracle.coherence.common.events.Event;

/**
 * <p>A {@link LifecycleEvent} is use to capture {@link Event}s relating to the lifecycle
 * of a particular source object.</p>
 * 
 * <p>A good example of {@link LifecycleEvent}s are those that relate to monitoring the lifecycle
 * of a Coherence {@link com.tangosol.net.Service}.</p>
 * 
 * @param <S> The source type
 * 
 * @author Brian Oliver
 */
public interface LifecycleEvent<S> extends Event
{
    /**
     * <p>Returns the source of the {@link LifecycleEvent}. ie: the object on which the 
     * {@link LifecycleEvent} was observed.</p>
     * 
     * @return The source of the {@link LifecycleEvent}
     */
    public S getSource();
}
