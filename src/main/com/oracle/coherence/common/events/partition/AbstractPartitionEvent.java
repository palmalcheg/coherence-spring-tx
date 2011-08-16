/*
 * File: AbstractPartitionEvent.java
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

import com.tangosol.net.PartitionedService;
import com.tangosol.net.partition.PartitionSet;

/**
 * <p>A base implementation of {@link PartitionEvent}s.</p>
 * 
 * @author Brian Oliver
 */
abstract class AbstractPartitionEvent implements PartitionEvent
{

    /**
     * <p>The {@link PartitionedService} on which the {@link PartitionEvent} occurred.</p>
     */
    private PartitionedService partitionedService;

    /**
     * <p>The set of partitions on which the {@link PartitionEvent} occurred.</p>
     */
    private PartitionSet partitionSet;

    /**
     * <p>Standard Constructor when using a Tangosol-based {@link com.tangosol.net.partition.PartitionEvent}.</p>
     * 
     * @param partitionEvent The Tangosol based {@link com.tangosol.net.partition.PartitionEvent}
     */
    protected AbstractPartitionEvent(com.tangosol.net.partition.PartitionEvent partitionEvent)
    {
        this.partitionedService = partitionEvent.getService();
        this.partitionSet = partitionEvent.getPartitionSet();
    }

    /**
     * <p>Standard Constructor when using explicit {@link PartitionedService} and {@link PartitionSet}s.</p>
     * 
     * @param partitionedService The {@link PartitionedService}
     * @param partitionSet       The {@link PartitionSet}
     */
    protected AbstractPartitionEvent(PartitionedService partitionedService,
                                     PartitionSet partitionSet)
    {
        this.partitionedService = partitionedService;
        this.partitionSet = partitionSet;
    }

    /**
     * {@inheritDoc}
     */
    public PartitionedService getService()
    {
        return partitionedService;
    }

    /**
     * {@inheritDoc}
     */
    public PartitionSet getPartitionSet()
    {
        return partitionSet;
    }
}
