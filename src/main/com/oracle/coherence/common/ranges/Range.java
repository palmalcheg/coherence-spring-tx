/*
 * File: Range.java
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

import java.util.Iterator;

/**
 * <p>A {@link Range} represents an immutable sequence of increasing long integer values.</p>
 * 
 * @author Brian Oliver
 */
public interface Range extends Iterable<Long>
{

    /**
     * <p>Returns the start of the {@link Range}.</p>
     * 
     * @return the start of the {@link Range}
     */
    public long getFrom();

    /**
     * <p>Returns the end of the {@link Range}.</p>
     * 
     * @return the end of the {@link Range}
     */
    public long getTo();

    /**
     * <p>Returns the number of values in the {@link Range}.</p>
     * 
     * @return the number of values in the {@link Range}
     */
    public long size();

    /**
     * <p>Returns if the {@link Range} is empty.</p>
     * 
     * @return if the {@link Range} is empty
     */
    public boolean isEmpty();

    /**
     * <p>Returns if the {@link Range} is a singleton range.  That is
     * the number of values in the range is a single value, starting at
     * 'x' and finishing at 'x'.</p>
     * 
     * @return if the {@link Range} is a singleton range
     */
    public boolean isSingleton();

    /**
     * <p>Returns if the {@link Range} contains the specified value.</p>
     * 
     * @param value the value to look for
     * 
     * @return if the {@link Range} contains the specified value
     */
    public boolean contains(long value);

    /**
     * <p>Returns if this {@link Range} intersects with another.</p>
     * 
     * @param other the other {@link Range}nge to check
     * 
     * @return if this {@link Range} intersects with another
     */
    public boolean intersects(Range other);

    /**
     * <p>Returns if this {@link Range} is adjacent to another.</p>
     * 
     * @param other the other {@link Range} to check
     * 
     * @return if this {@link Range} is adjacent to another
     */
    public boolean isAdjacent(Range other);

    /**
     * <p>Creates and returns a new {@link Range} representing the union of
     * this and another {@link Range}.</p>
     * 
     * @param other the other {@link Range}
     * 
     * @return a new {@link Range} representing the union of
     * this and another {@link Range}
     */
    public Range union(Range other);

    /**
     * <p>Creates and returns a new {@link Range} based on the 
     * current {@link Range} but with the specified value added.</p>
     * 
     * @param value the value to add
     * 
     * @return a new {@link Range} based on the current {@link Range} with the specified value added
     */
    public Range add(long value);

    /**
     * <p>Creates and returns a new {@link Range} based on this {@link Range}
     * but with the specified value removed.</p>
     * 
     * @param value the value to remove
     * 
     * @return returns a new {@link Range} based on this {@link Range} with the specified value removed
     */
    public Range remove(long value);

    /**
     * <p>Returns an {@link Iterator} over the values in a {@link Range},
     * in order from lowest value to the highest value inclusive.<p>
     * 
     * @return an {@link Iterator} over the values in a {@link Range}
     */
    public Iterator<Long> iterator();
}
