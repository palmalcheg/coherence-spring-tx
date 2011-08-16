/*
 * File: SparseRange.java
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
import java.util.TreeSet;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

import com.tangosol.util.ExternalizableHelper;

/**
* <p>A {@link SparseRange} is a useful data structure for representing 
* an immutable range of increasing long integer values.</p>
* 
* <p>Internally a {@link SparseRange} is an ordered non-intersecting set
* of sub-{@link ContiguousRange}s.</p>
* 
* @author Brian Oliver
*/

@SuppressWarnings("serial")
public class SparseRange implements Range, ExternalizableLite, PortableObject, Iterable<Long>

{

    /**
    * <p>The starting point for the {@link Range}.</p>
    */
    private long m_lFrom;

    /**
    * <p>The collection of values in the {@link Range} (in order)</p>
    * 
    * <p>This may be <code>null</code> if the {@link Range} is empty.</p>
    */
    private TreeSet<ContiguousRange> m_oSubRanges;

    /**
    * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
    */
    public SparseRange()
    {
        this.m_oSubRanges = new TreeSet<ContiguousRange>();
    }

    /**
    * <p>Package Level Constructor for an empty {@link SparseRange}
    * commencing at a specified value.</p>
    * 
    * @param from the start of the range
    */
    SparseRange(long from)
    {
        this.m_lFrom = from;
        this.m_oSubRanges = new TreeSet<ContiguousRange>();
    }

    /**
    * <p>Package Level Constructor based on two non-intersecting 
    * {@link ContiguousRange}s.</p>
    * 
    * @param contiguousRange1 the first {@link ContiguousRange} to use
    * @param contiguousRange2 the second {@link ContiguousRange} to use
    */
    SparseRange(ContiguousRange contiguousRange1,
                ContiguousRange contiguousRange2)
    {
        if (contiguousRange1.isEmpty() && contiguousRange2.isEmpty())
        {
            this.m_lFrom = Math.min(contiguousRange1.getFrom(), contiguousRange2.getFrom());
            this.m_oSubRanges = new TreeSet<ContiguousRange>();
        }
        else
        {
            if (contiguousRange1.isEmpty())
            {
                this.m_lFrom = contiguousRange2.getFrom();
                this.m_oSubRanges = new TreeSet<ContiguousRange>();
                this.m_oSubRanges.add(contiguousRange2);
            }
            else
            {
                if (contiguousRange2.isEmpty())
                {
                    this.m_lFrom = contiguousRange1.getFrom();
                    this.m_oSubRanges = new TreeSet<ContiguousRange>();
                    this.m_oSubRanges.add(contiguousRange1);
                }
                else
                {
                    this.m_oSubRanges = new TreeSet<ContiguousRange>();
                    //If the two ranges we are adding are adjacent - we should make a union out of them
                    if (contiguousRange1.isAdjacent(contiguousRange2) || contiguousRange1.intersects(contiguousRange2))
                    {
                        this.m_oSubRanges.add((ContiguousRange) contiguousRange1.union(contiguousRange2));
                    }
                    else
                    {
                        this.m_oSubRanges.add(contiguousRange1);
                        this.m_oSubRanges.add(contiguousRange2);
                    }
                    this.m_lFrom = this.m_oSubRanges.first().getFrom();
                }
            }
        }
    }

    /**
    * <p>Private Constructor to create a {@link SparseRange} based on
    * a on-empty {@link TreeSet} of {@link ContiguousRange}s.</p>
    * 
    * @param contiguousRanges the {@link TreeSet} to base this {@link SparseRange} on
    */
    private SparseRange(TreeSet<ContiguousRange> contiguousRanges)
    {
        assert !contiguousRanges.isEmpty();
        this.m_oSubRanges = contiguousRanges;
        this.m_lFrom = m_oSubRanges.first().getFrom();
    }

    /**
    * {@inheritDoc}
    */
    public long getFrom()
    {
        return isEmpty() ? m_lFrom : m_oSubRanges.first().getFrom();
    }

    /**
    * {@inheritDoc}
    */
    public long getTo()
    {
        return isEmpty() ? m_lFrom - 1 : m_oSubRanges.last().getTo();
    }

    /**
    * {@inheritDoc}
    */
    public long size()
    {
        long size = 0;
        for (ContiguousRange range : m_oSubRanges)
            size = size + range.size();
        return size;
    }

    /**
    * {@inheritDoc}
    */
    public boolean isEmpty()
    {
        return m_oSubRanges.isEmpty();
    }

    /**
    * {@inheritDoc}
    */
    public boolean isSingleton()
    {
        return m_oSubRanges.size() == 1 && m_oSubRanges.first().isSingleton();
    }

    /**
    * {@inheritDoc}
    */
    public boolean contains(long value)
    {
        for (ContiguousRange range : m_oSubRanges)
        {
            if (range.contains(value))
                return true;
        }
        return false;
    }

    /**
    * Test whether a {@link Range}ge is adjacent to any of the subranges.
    * 
    * @param other the other {@link Range} to test against
    * 
    * @return whether a {@link Range}e is adjacent to any of the subranges
    */
    private boolean testAdjacency(Range other)
    {
        for (ContiguousRange range : m_oSubRanges)
        {
            if (other.isAdjacent(range))
                return true;
        }
        return false;
    }

    /**
    * {@inheritDoc}
    */
    public boolean isAdjacent(Range other)
    {
        return this.isEmpty() || other.isEmpty() || other instanceof InfiniteRange || testAdjacency(other);
    }

    /**
    * {@inheritDoc}
    */
    public boolean intersects(Range other)
    {
        return this.isEmpty() || other.isEmpty() || other instanceof InfiniteRange || testIntersection(other);
    }

    /**
    * Tests the intersection of a SparseRange with a ContigousRange and another SparseRange.
    * 
    * @param other is the other Range
    * 
    * @return true if intersecting
    */
    private boolean testIntersection(Range other)
    {
        if (other instanceof ContiguousRange)
        {
            return (this.contains(other.getFrom()) || this.contains(other.getTo()) || other.contains(this.getFrom()) || other
                .contains(this.getTo()));
        }
        else
        {
            if (other instanceof SparseRange)
            {
                for (ContiguousRange range : m_oSubRanges)
                {
                    if (other.intersects(range))
                        return true;
                }
                return false;
            }
            else
            {
                return false; 
            }
        }
    }

    /**
    * {@inheritDoc}
    */
    public Range add(long value)
    {
        return union(new ContiguousRange(value, value));
    }

    /**
    * {@inheritDoc}
    */
    public Range remove(long value)
    {
        TreeSet<ContiguousRange> resultingSubRanges = new TreeSet<ContiguousRange>();
        for (ContiguousRange range : m_oSubRanges)
        {
            if (range.contains(value))
            {
                Range resultingRange = range.remove(value);
                if (resultingRange.isEmpty())
                {
                    //nothing to do here... as the resulting range is empty
                }
                else
                {
                    if (resultingRange instanceof ContiguousRange)
                    {
                        resultingSubRanges.add((ContiguousRange) resultingRange);
                    }
                    else
                    {
                        resultingSubRanges.add(((SparseRange) resultingRange).m_oSubRanges.first());
                        resultingSubRanges.add(((SparseRange) resultingRange).m_oSubRanges.last());
                    }
                }
            }
            else
            {
                resultingSubRanges.add(range);
            }
        }

        if (resultingSubRanges.isEmpty())
        {
            return new ContiguousRange(value + 1);
        }
        else
        {
            if (resultingSubRanges.size() == 1)
            {
                return resultingSubRanges.first();
            }
            else
            {
                return new SparseRange(resultingSubRanges);
            }
        }
    }

    /**
    * {@inheritDoc}
    */
    public Range union(Range other)
    {
        if (other.isEmpty())
        {
            return this;
        }
        else
        {
            if (this.isEmpty())
            {
                return other;
            }
            else
            {
                if (other instanceof InfiniteRange)
                {
                    return other;
                }
                else
                {
                    Iterator<ContiguousRange> leftSubRanges = this.m_oSubRanges.iterator();
                    Iterator<ContiguousRange> rightSubRanges;
                    if (other instanceof ContiguousRange)
                    {
                        TreeSet<ContiguousRange> temp = new TreeSet<ContiguousRange>();
                        temp.add((ContiguousRange) other);
                        rightSubRanges = temp.iterator();
                    }
                    else
                    {
                        rightSubRanges = ((SparseRange) other).m_oSubRanges.iterator();
                    }

                    //merge the two sets of sub ranges
                    TreeSet<ContiguousRange> resultingSubRanges = new TreeSet<ContiguousRange>();
                    ContiguousRange left = null;
                    ContiguousRange right = null;
                    ContiguousRange current = leftSubRanges.next();
                    while (leftSubRanges.hasNext() || rightSubRanges.hasNext())
                    {
                        if (left == null && leftSubRanges.hasNext())
                            left = leftSubRanges.next();
                        if (right == null && rightSubRanges.hasNext())
                            right = rightSubRanges.next();
                        if (left != null && (current.intersects(left) || current.isAdjacent(left)))
                        {
                            current = (ContiguousRange) current.union(left);
                            left = null;
                        }
                        else
                        {
                            if (right != null && (current.intersects(right) || current.isAdjacent(right)))
                            {
                                current = (ContiguousRange) current.union(right);
                                right = null;
                                //the resulting current can still be either adjacent to or intersect left
                                if (left != null && (current.intersects(left) || current.isAdjacent(left)))
                                {
                                    current = (ContiguousRange) current.union(left);
                                    left = null;
                                }

                            }
                            else
                            {
                                resultingSubRanges.add(current);
                                if (left == null)
                                {
                                    current = right;
                                    right = null;
                                }
                                else
                                {
                                    current = left;
                                    left = null;
                                }
                            }
                        }
                    }

                    if (left != null)
                    {
                        resultingSubRanges.add(left);
                    }
                    if (right != null)
                    {
                        resultingSubRanges.add(right);
                    }
                    if (current != null)
                    {
                        resultingSubRanges.add(current);
                    }
                    //create the resulting range
                    if (resultingSubRanges.isEmpty())
                    {
                        return Ranges.EMPTY;
                    }
                    else
                    {
                        if (resultingSubRanges.size() == 1)
                        {
                            return resultingSubRanges.first();
                        }
                        else
                        {
                            return new SparseRange(resultingSubRanges);
                        }
                    }
                }
            }
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
    * Iterates over all subranges and checks for equality.
    * @param other the range to compare for equality.
    * @return true if equal
    */
    private boolean iterateForEquality(Range other)
    {
        if (other instanceof ContiguousRange)
        {
            //If the other range is a ContiguousRange then we are equal if there is one subrange
            //that is equal to the other range
            ContiguousRange otherrange = (ContiguousRange) other;
            Iterator<ContiguousRange> thisSubRanges = this.m_oSubRanges.iterator();
            if (thisSubRanges.hasNext())
            {
                Range range = thisSubRanges.next();
                if (!range.equals(otherrange))
                {
                    return false;
                }
                return (!thisSubRanges.hasNext()); //If we don't have any more subranges we are equal
            }
        }

        if (other instanceof SparseRange)
        {
            SparseRange othersparse = (SparseRange) other;
            Iterator<ContiguousRange> thisSubRanges = this.m_oSubRanges.iterator();
            Iterator<ContiguousRange> otherSubRanges = othersparse.m_oSubRanges.iterator();

            while (thisSubRanges.hasNext() && otherSubRanges.hasNext())
            {
                if (!thisSubRanges.next().equals(otherSubRanges.next()))
                    return false;
            }
            return (thisSubRanges.hasNext() == otherSubRanges.hasNext()); //we must still check if only one range ran out
        }
        else
        {
            if (other instanceof InfiniteRange)
            {
                return false;
            }
            else
            {
                return false; //we don't support any other Ranges
            }
        }
    }

    /**
    * {@inheritDoc}
    */
    public boolean equals(Object object)
    {
        if (object != null && object instanceof Range)
        {
            Range other = (Range) object;
            return iterateForEquality(other);
        }
        else
        {
            return false;
        }
    }

    /**
    * {@inheritDoc}
    */
    public String toString()
    {
        return isEmpty() ? "SparseRange[]" : String.format("SparseRange[%s]", m_oSubRanges);
    }

    /**
    * {@inheritDoc}
    */
    public void readExternal(DataInput in) throws IOException
    {
        this.m_lFrom = ExternalizableHelper.readLong(in);
        this.m_oSubRanges = new TreeSet<ContiguousRange>();
        ExternalizableHelper.readCollection(in, m_oSubRanges, this.getClass().getClassLoader());
    }

    /**
    * {@inheritDoc}
    */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeLong(out, m_lFrom);
        ExternalizableHelper.writeCollection(out, m_oSubRanges);
    }

    /**
    * {@inheritDoc}
    */
    public void readExternal(PofReader reader) throws IOException
    {
        this.m_lFrom = reader.readLong(0);
        reader.readCollection(1, m_oSubRanges);
    }

    /**
    * {@inheritDoc}
    */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeLong(0, m_lFrom);
        writer.writeCollection(1, m_oSubRanges);
    }

    /**
    * <p>An implementation of an {@link Iterator} so that we
    * can iterate over the values in a {@link SparseRange}.</p>
    */
    class RangeIterator implements Iterator<Long>
    {

        /**
        * <p>An {@link Iterator} over the {@link ContiguousRange}s in the {@link SparseRange}.</p>
        */
        private Iterator<ContiguousRange> ranges;

        /**
        * <p>An {@link Iterator} over the values in the current {@link ContiguousRange}.</p>
        */
        private Iterator<Long> iterator;

        /**
        * <p>Standard Constructor.</p>
        * 
        * @param sparseRange the {@link SparseRange} to iterate over
        */
        RangeIterator(SparseRange sparseRange)
        {
            this.ranges = sparseRange.m_oSubRanges.iterator();
            if (ranges.hasNext())
            {
                this.iterator = this.ranges.next().iterator();
            }
            else
            {
                this.iterator = null;
            }
        }

        /**
        * {@inheritDoc}
        */
        public boolean hasNext()
        {
            return (this.iterator != null && this.iterator.hasNext()) || (this.ranges.hasNext());
        }

        /**
        * {@inheritDoc}
        */
        public Long next()
        {
            if (!iterator.hasNext())
                iterator = this.ranges.next().iterator();
            return iterator.next();
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
