/*
 * File: Leasing.java
 * 
 * Copyright (c) 2008. All Rights Reserved. Oracle Corporation.
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
 * <p>A set of static methods to help with the construction of {@link Lease}s.</p>
 * 
 * @author Brian Oliver
 */
public final class Leasing
{

    /**
     * <p>Creates and returns a new indefinite {@link Lease} with the acquisition time being the system time at the time 
     * of the call.</p>
     * 
     * @return a new indefinite {@link Lease} with the  acquisition time being the system time at the time of the call.
     */
    public static Lease newIndefiniteLease()
    {
        return new Lease(-3);
    }

    /**
     * <p>Creates and returns a new {@link Lease} with the specified duration (in ms) from the time this method was 
     * called.</p>
     * 
     * @param duration the duration in ms when the {@link Lease} should expire
     * 
     * @return a new {@link Lease} with the specified duration (in ms) from the time this method was called
     */
    public static Lease newLease(long duration)
    {
        return new Lease(duration);
    }

    /**
     * <p>Creates and returns a new {@link Lease} with the specified acquisition time (from the EPOC) and duration 
     * (in ms).</p>
     * 
     * @param acquisitionTime the time the {@link Lease} was acquired
     * @param duration        the time in ms that the {@link Lease} should last beyond acquisitionTime
     * 
     * @return a new {@link Lease} with the specified acquisition time (from the EPOC) and duration (in ms)
     */
    public static Lease newLease(long acquisitionTime,
                                 long duration)
    {
        return new Lease(acquisitionTime, duration);
    }
}
