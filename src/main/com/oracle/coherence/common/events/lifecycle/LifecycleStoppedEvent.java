/*
 * File: LifecycleStoppedEvent.java
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

/**
 * <p>The {@link LifecycleStoppedEvent} represents that a source that has just stopped 
 * in it's lifecycle.</p>
 * 
 * @author Brian Oliver
 *
 * @param <S> The source type
 */
public class LifecycleStoppedEvent<S> extends AbstractLifecycleEvent<S>
{
    /**
     * <p>Standard Constructor.</p>
     * 
     * @param source The source of the {@link LifecycleEvent}.
     */
    public LifecycleStoppedEvent(S source)
    {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("LifecycleStoppedEvent{source=%s}", getSource());
    }
}
