/*
 * File: SequenceGenerator.java
 * 
 * Copyright (c) 2009. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.sequencegenerators;

import com.oracle.coherence.common.ranges.Range;

/**
 * <p>A {@link SequenceGenerator} may be used to generate one-or-more
 * non-overlapping monotonically increasing long numbers.</p>
 * 
 * <p>NOTE: All {@link SequenceGenerator} implementations <strong>must</strong>
 * be thread-safe. An individual {@link SequenceGenerator} must be capable of
 * being used by multiple-threads simultaneously without corrupting the underlying
 * sequence.</p>
 * 
 * @author Brian Oliver
 */
public interface SequenceGenerator
{

    /**
     * <p>Creates and returns the next long from the sequence.</p>
     * 
     * @return The next long from a sequence of longs.
     */
    public long next();


    /**
     * <p>Creates and returns a {@link Range} of monotonically increasing numbers
     * from the sequence, the resulting {@link Range} being of length sequenceSize.</p>
     * 
     * @param sequenceSize The number of values in the resulting {@link Range}.
     * 
     * @return A {@link Range} of longs with the specified size.
     */
    public Range next(long sequenceSize);
}
