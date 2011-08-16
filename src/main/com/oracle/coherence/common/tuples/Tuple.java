/*
 * File: Tuple.java
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
package com.oracle.coherence.common.tuples;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PortableObject;

/**
 * <p>An immutable sequence of serializable values.</p>
 * 
 * @author Brian Oliver
 */
public interface Tuple extends ExternalizableLite, PortableObject
{
    /**
     * <p>Return the number of values in the {@link Tuple}.</p>
     * 
     * @return The number of values in the {@link Tuple}.
     */
    public int size();


    /**
     * Return the value at index.  The first value is at index 0.
     * 
     * @param index The position of the value to return.
     * 
     * @return The value at the specified position in the {@link Tuple}.
     * 
     * @throws IndexOutOfBoundsException When 0 < index <= size()
     */
    public Object get(int index) throws IndexOutOfBoundsException;
}
