/*
 * File: LeaseListener.java
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
package com.oracle.coherence.common.leasing;

/**
 * <p>A {@link LeaseListener} specifies call-back methods
 * a {@link LeaseExpiryCoordinator} will use when a {@link Lease} expires, is canceled
 * or is suspended.</p>
 * 
 * @author Brian Oliver
 */
public interface LeaseListener
{

    /**
     * <p>This method will be called when the specified {@link Lease} owned by the
     * specified lease owner has expired.</p>
     * 
     * @param leaseOwner the owner of the lease
     * @param lease      the {@link Lease}
     */
    public void onLeaseExpiry(Object leaseOwner,
                              Lease lease);

    /**
     * <p>This method will be called when the specified {@link Lease} owned by the
     * specified lease owner has been canceled.</p>
     * 
     * @param leaseOwner the owner of the lease
     * @param lease      the {@link Lease}
     */
    public void onLeaseCanceled(Object leaseOwner,
                                Lease lease);

    /**
     * <p>This method will be called when the specified {@link Lease} owned by the
     * specified lease owner has been suspended.</p>
     * 
     * @param leaseOwner the owner of the lease
     * @param lease      the {@link Lease}
     */
    public void onLeaseSuspended(Object leaseOwner,
                                 Lease lease);
}
