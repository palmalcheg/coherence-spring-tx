/*
 * File: PartitionEvent.java
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
package com.oracle.coherence.common.events.partition;

import com.oracle.coherence.common.events.Event;
import com.tangosol.net.PartitionedService;
import com.tangosol.net.partition.PartitionSet;

/**
 * <p>{@link PartitionEvent}s capture information concerning actions performed on or with Coherence Partitions, typically relating to
 * the {@link PartitionedService}s that manage partitioned caches.</p>
 * 
 * @author Brian Oliver
 */
public interface PartitionEvent extends Event
{
    /**
     * <p>Returns the {@link PartitionedService} on which the {@link PartitionEvent} occurred.</p>
     * 
     * @return the {@link PartitionedService} on which the {@link PartitionEvent} occurred
     */
    public PartitionedService getService();

    /**
     * <p>Returns the {@link PartitionSet} to which the {@link PartitionEvent} applies.</p>
     * 
     * @return the {@link PartitionSet} to which the {@link PartitionEvent} applies
     */
    public PartitionSet getPartitionSet();
}
