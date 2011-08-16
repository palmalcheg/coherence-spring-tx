/*
 * File: PartitionTransferEvent.java
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

import com.tangosol.net.Member;

/**
 * <p>A {@link PartitionTransferEvent} provide information about the source {@link Member} and destination
 * {@link Member} when one or more partitions are being transferred.</p>
 * 
 * @author Brian Oliver
 */
public interface PartitionTransferEvent extends PartitionEvent
{

    /**
     * <p>The {@link Member} from which the set of partitions is moving.</p>
     * 
     * @return the {@link Member} from which the set of partitions is moving.
     */
    public Member getMemberFrom();

    /**
     * <p>The {@link Member} to which the set of partitions is moving.</p>
     * 
     * @return the {@link Member} to which the set of partitions is moving.
     */
    public Member getMemberTo();

}
