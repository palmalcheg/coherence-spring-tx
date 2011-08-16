/*
 * File: Cause.java
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

/**
 * <p>An enumeration to represent the possible causes of backing map events.</p>
 * 
 * @author Brian Oliver
 */
public enum Cause
{
    /**
     * <p><code>Regular</code> is for regular inserts, updates and delete events.</p>
     */
    Regular,

    /**
     * <p><code>Eviction</code> is for deletes that are due to cache eviction.</p>
     */
    Eviction,

    /**
     * <p><code>PartitionManagement</code> is used for inserts and deletes that
     * have occurred due to cache partitions being load-balanced or recovered.</p> 
     */
    PartitionManagement,

    /**
     * <p><code>StoreCompleted</code> is for update events due to a storage decoration 
     * change on an entry. Coherence updates a decoration after a successful store
     * operation on a write-behind store. ie: an asynchronous store has completed.</p> 
     */
    StoreCompleted;
}
