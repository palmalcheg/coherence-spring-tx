/*
 * File: PartitionDepartingEvent.java
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

import com.oracle.coherence.common.events.AbortablePhasedEvent;
import com.tangosol.net.partition.PartitionEvent;

/**
 * <p>A {@link PartitionArrivingEvent} is a phased {@link PartitionTransferEvent} that captures the information
 * concerning the departure (ie: the loss of ownership) of a set of partitions.</p>
 * 
 * @author Brian Oliver
 */
public class PartitionDepartingEvent extends AbstractPartitionTransferEvent implements AbortablePhasedEvent
{

    /**
     * <p>The {@link Phase} of the {@link PartitionTransferEvent}.</p>
     */
    private Phase phase;

    /**
     * <p>Standard Constructor for Tangosol-based {@link PartitionEvent}s.</p>
     * 
     * @param partitionEvent the Tangosol based {@link PartitionEvent}
     */
    public PartitionDepartingEvent(PartitionEvent partitionEvent)
    {
        super(partitionEvent);
        switch (partitionEvent.getId())
        {
            case PartitionEvent.PARTITION_TRANSMIT_BEGIN:
                this.phase = Phase.Commenced;
                break;
            case PartitionEvent.PARTITION_TRANSMIT_COMMIT:
                this.phase = Phase.Completed;
                break;
            case PartitionEvent.PARTITION_TRANSMIT_ROLLBACK:
                this.phase = Phase.Aborted;
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Phase getPhase()
    {
        return phase;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("PartitionDepartingEvent{partitionSet=%s, phase=%s, fromMember=%s, toMember=%s}",
            getPartitionSet(), getPhase(), getMemberFrom(), getMemberTo());
    }
}
