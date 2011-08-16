/*
 * File: MultiplexingBackingMapListener.java
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
package com.oracle.coherence.common.backingmaplisteners;

import com.tangosol.util.MapEvent;

/**
 * <p>A {@link MultiplexingBackingMapListener} defines a simplified {@link com.tangosol.util.MapListener} that
 * is designed to be used to receive events from backing maps (ie: the underlying mechanism
 * often used by Coherence to store objects).</p>
 *  
 * <p>Backing {@link com.tangosol.util.MapListener}s are embeddable {@link com.tangosol.util.MapListener}s that are 
 * injected into Coherence Cache members (storage-enabled) for the purpose of
 * handling events directly in-process of the primary partitions (of distributed schemes).</p>
 * 
 * <p>They are extremely useful for performing in-process processing of events within Coherence itself.</p>
 * 
 * @author Brian Oliver
 */
public interface MultiplexingBackingMapListener
{

    /**
     * <p>Implement this method to handle call-backs when {@link MapEvent}s 
     * occur on a Backing Map.</p>
     * 
     * <p>NOTE: The implementing typically has to be registered or configured
     * some where to recieve events.</p>
     * 
     * @param mapEvent A standard mapEvent
     * @param cause The underlying cause of the event
     */
    public void onBackingMapEvent(MapEvent mapEvent,
                                  Cause cause);
}
