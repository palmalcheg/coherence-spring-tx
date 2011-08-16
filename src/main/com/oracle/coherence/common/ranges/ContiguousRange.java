/*
 * File: ContiguousRange.java
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
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link ContiguousRange} is a useful data structure for representing 
 * an immutable range of monotonically increasing long integer values.</p>
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class ContiguousRange implements Range, ExternalizableLite, PortableObject, Iterable<Long>, Comparable<Range>
{

    /**
     * <p>The starting value in the {@link ContiguousRange}.</p>
     */
    private long from;

    /**
     * <p>The ending value in the {@link ContiguousRange} (inclusive).</p>
     */
    private long to;

    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public ContiguousRange()
    {
        this.from = 0;
        this.to = from - 1;

    }

    /**
     * <p>The Standard Constructor.</p>
     * 
     * @param from the starting value
     * @param to   the ending value
     */
    ContiguousRange(long from,
                    long to)
    {
        this.from = from;
        this.to = to;
    }

    /**
     * <p>The Standard Constructor
     * (for empty ranges starting at the specified value).</p>
     * 
     * @param from the starting value
     */
    ContiguousRange(long from)
    {
        this.from = from;
        this.to = from - 1;
    }

    /**
     * {@inheritDoc}
     */
    public long getFrom()
    {
        return from;
    }

    /**
     * {@inheritDoc}
     */
    public long getTo()
    {
        return to;
    }

    /**
     * {@inheritDoc}
     */
    public long size()
    {
        return isEmpty() ? 0 : to - from + 1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return from > to;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton()
    {
        return from == to;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(long value)
    {
        return !isEmpty() && value >= getFrom() && value <= getTo();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAdjacent(Range other)
    {
        return this.isEmpty() || other.isEmpty() || other instanceof InfiniteRange
                || this.getFrom() == other.getTo() + 1 || this.getTo() == other.getFrom() - 1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean intersects(Range other)
    {
        return this.isEmpty() || other.isEmpty() || other instanceof InfiniteRange || this.contains(other.getFrom())
                || this.contains(other.getTo());
    }

    /**
     * {@inheritDoc}
     */
    public Range union(Range other)
    {
        if (isEmpty())
        {
            return other;

        }
        else if (other.isEmpty())
        {
            return this;

        }
        else if (other instanceof ContiguousRange)
        {
            if (this.intersects(other) || this.isAdjacent(other))
            {
                return new ContiguousRange(Math.min(this.getFrom(), other.getFrom()), Math.max(this.getTo(), other
                    .getTo()));
            }
            else
            {
                return new SparseRange(this, (ContiguousRange) other);
            }

        }
        else if (other instanceof InfiniteRange)
        {
            return other;

        }
        else
        {
            return other.union(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Range remove(long value)
    {
        if (isEmpty())
        {
            return this;

        }
        else if (getFrom() == value)
        {
            return new ContiguousRange(getFrom() + 1, getTo());

        }
        else if (getTo() == value)
        {
            return new ContiguousRange(getFrom(), getTo() - 1);

        }
        else if (contains(value))
        {
            return new SparseRange(new ContiguousRange(getFrom(), value - 1), new ContiguousRange(value + 1, getTo()));

        }
        else
        {
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Range add(long value)
    {
        if (isEmpty())
        {
            return new ContiguousRange(value, value);
        }
        else if (contains(value))
        {
            return this;
        }
        else if (value == getFrom() - 1)
        {
            return new ContiguousRange(value, to);
        }
        else if (value == getTo() + 1)
        {
            return new ContiguousRange(from, value);
        }
        else
        {
            return new SparseRange(this, new ContiguousRange(value, value));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Long> iterator()
    {
        return new RangeIterator(this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object)
    {
        if (object != null && object instanceof Range)
        {
            Range other = (Range) object;
            return this.getFrom() == other.getFrom() && this.getTo() == other.getTo() && this.size() == other.size();
        }
        else
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Range other)
    {
        if ((other instanceof ContiguousRange) && ((equals(other)) ^ (!intersects((ContiguousRange) other))))
        {
            if (this.getFrom() < other.getFrom())
                return -1;
            else if (this.getFrom() > other.getFrom())
                return +1;
            else
                return 0;
        }
        else
        {
            throw new NotComparableRuntimeException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return isEmpty() ? "ContiguousRange[]" : isSingleton() ? String.format("ContiguousRange[%d]", from) : String
            .format("ContiguousRange[%d..%d]", from, to);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.from = ExternalizableHelper.readLong(in);
        this.to = ExternalizableHelper.readLong(in);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeLong(out, from);
        ExternalizableHelper.writeLong(out, to);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.from = reader.readLong(0);
        this.to = reader.readLong(1);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeLong(0, from);
        writer.writeLong(1, to);
    }

    /**
     * <p>An implementation of an {@link Iterator} so that we
     * can iterate over the values in a {@link ContiguousRange}.</p>
     */
    class RangeIterator implements Iterator<Long>
    {

        /**
         * <p>The {@link ContiguousRange} we are iterating over.</p>
         */
        private ContiguousRange range;

        /**
         * <p>The next value in the iteration.</p>
         */
        private long next;

        /**
         * <p>Standard Constructor.</p>
         * 
         * @param range the {@link ContiguousRange} we are iterating over
         */
        public RangeIterator(ContiguousRange range)
        {
            this.range = range;
            this.next = range.getFrom();
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext()
        {
            return !range.isEmpty() && next <= range.getTo();
        }

        /**
         * {@inheritDoc}
         */
        public Long next()
        {
            return next++;
        }

        /**
         * {@inheritDoc}
         */
        public void remove()
        {
            throw new UnsupportedOperationException(
                "Can't remove values from Range implementations as they are immutable");
        }
    }
}
