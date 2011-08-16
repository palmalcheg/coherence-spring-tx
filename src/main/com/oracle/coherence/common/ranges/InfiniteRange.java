/*
 * File: InfiniteRange.java
 * 
 * Copyright (c) 2008-2010. All Rights Reserved. Oracle Corporation.
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * <p>An {@link InfiniteRange} represents the range of all possible long integer values.</p>
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class InfiniteRange implements Range, ExternalizableLite, PortableObject, Iterable<Long>, Comparable<Range>
{

    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public InfiniteRange()
    {
        //nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    public long getFrom()
    {
        return Long.MIN_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public long getTo()
    {
        return Long.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public Range add(long value)
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(long value)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean intersects(Range other)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAdjacent(Range other)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public long size()
    {
        return Long.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public Range union(Range other)
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Range remove(long value)
    {
        throw new UnsupportedOperationException("Can't remove a value from an InfiniteRange");
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Long> iterator()
    {
        throw new UnsupportedOperationException("Can't create an Iterator over an InfiniteRange");
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Range other)
    {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "InfiniteRange[...]";
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        //nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        //nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        //nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        //nothing to do here
    }
}
