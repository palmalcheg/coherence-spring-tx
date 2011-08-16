/*
 * File: Ranges.java
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
package com.oracle.coherence.common.ranges;

/**
 * <p>A set of static methods to help with the construction of {@link Range}s.</p>
 * 
 * @author Brian Oliver
 */
public final class Ranges
{

    /**
     * <p>No instances of a {@link Ranges} class is permitted.</p>
     */
    private Ranges()
    {
    }

    /**
     * <p>A constant representing an empty {@link Range} starting at 0.</p>
     */
    public static Range EMPTY = new ContiguousRange(0);

    /**
     * <p>A constant representing an infinite {@link Range} of values.</p>
     */
    public static Range INFINITE = new InfiniteRange();

    /**
     * <p>A helper method to construct an empty {@link Range} commencing
     * at a specified value.</p>
     * 
     * @param from the start of the range
     * 
     * @return an empty {@link Range} commencing at a specified value
     */
    public static Range newEmptyRangeFrom(long from)
    {
        return new ContiguousRange(from);
    }

    /**
     * <p>A helper method to construct a {@link Range} with only a single 
     * value.</p>
     * 
     * @param value the value to have in the {@link Range}
     * 
     * @return a {@link Range} with only a single
     */
    public static Range newSingletonRange(long value)
    {
        return new ContiguousRange(value, value);
    }

    /**
     * <p>A helper method to construct a {@link Range} between (and including)
     * two values.</p>
     * 
     * @param from the start of the {@link Range}
     * @param to   the end of the {@link Range}
     * 
     * @return a {@link Range} between (and including) two values.
     */
    public static Range newRange(long from,
                                 long to)
    {
        return new ContiguousRange(Math.min(from, to), Math.max(from, to));
    }
}
