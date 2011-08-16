/*
 * File: ClusteredSequenceGenerator.java
 * 
 * Copyright (c) 2008-2009. All Rights Reserved. Oracle Corporation.
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.oracle.coherence.common.ranges.Range;
import com.oracle.coherence.common.ranges.Ranges;
import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * <p>A {@link ClusteredSequenceGenerator} is an implementation 
 * of a {@link SequenceGenerator} that uses Coherence to maintain
 * a cluster-scoped specifically named sequence 
 * (based on a Coherence Cluster).</p>
 *  
 * @author Brian Oliver
 */
public class ClusteredSequenceGenerator implements SequenceGenerator
{

    /**
     * <p>The name of the sequence.</p>
     */
    private String sequenceName;

    /**
     * <p>The initial value of the sequence to be used 
     * if the named sequence has not been defined in the cluster.</p>
     */
    private long initialValue;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param sequenceName The name of the sequence being maintained
     * @param initialValue The initial value of the sequence if it does not 
     *                     exist in the cluster.
     */
    public ClusteredSequenceGenerator(String sequenceName,
                                      long initialValue)
    {
        this.sequenceName = sequenceName;
        this.initialValue = initialValue;
    }


    /**
     * {@inheritDoc}
     */
    public long next()
    {
        return next(1).getFrom();
    }


    /**
     * {@inheritDoc}
     */
    public Range next(long sequenceSize)
    {
        NamedCache namedCache = CacheFactory.getCache(State.CACHENAME);
        long from = (Long) namedCache.invoke(sequenceName, new GenerateSequenceNumberProcessor(initialValue,
            sequenceSize));
        return Ranges.newRange(from, from + sequenceSize - 1);
    }

    /**
     * <p>The {@link State} class represents the next available sequence number
     * for a named {@link ClusteredSequenceGenerator} in a Coherence Cache.</p>
     */
    @SuppressWarnings("serial")
    public static class State implements ExternalizableLite, PortableObject
    {

        /**
         * <p>The name of the Coherence Cache that will store {@link ClusteredSequenceGenerator} {@link State}s.</p>
         */
        public static final String CACHENAME = "coherence.common.sequencegenerators";

        /**
         * <p>The next value that will be issued for the named {@link ClusteredSequenceGenerator}.</p>
         */
        private long nextValue;


        /**
         * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
         */
        public State()
        {
        }


        /**
         * <p>Standard Constructor.</p>
         * 
         * @param initialValue The initial value of the sequence.
         */
        public State(long initialValue)
        {
            this.nextValue = initialValue;
        }


        /**
         * <p>Generates a specified number of sequential ids and returns 
         * the first number in the sequence.</p>
         * 
         * @param sequenceSize The number of sequential ids to generate
         * 
         * @return The first value in the sequence 
         */
        public long generate(long sequenceSize)
        {
            long start = nextValue;
            nextValue = nextValue + sequenceSize;
            return start;
        }


        /**
         * {@inheritDoc}
         */
        public void readExternal(DataInput in) throws IOException
        {
            this.nextValue = ExternalizableHelper.readLong(in);
        }


        /**
         * {@inheritDoc}
         */
        public void writeExternal(DataOutput out) throws IOException
        {
            ExternalizableHelper.writeLong(out, nextValue);
        }


        /**
         * {@inheritDoc}
         */
        public void readExternal(PofReader reader) throws IOException
        {
            this.nextValue = reader.readLong(0);
        }


        /**
         * {@inheritDoc}
         */
        public void writeExternal(PofWriter writer) throws IOException
        {
            writer.writeLong(0, nextValue);
        }


        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return String.format("ClusteredSequenceGenerator.State{nextValue=%d}", nextValue);
        }
    }

    /**
     * <p>The {@link GenerateSequenceNumberProcessor} is used to
     * generate one-or-more sequence numbers from a named {@link ClusteredSequenceGenerator}
     * (using the {@link State}).</p>
     */
    @SuppressWarnings("serial")
    public static class GenerateSequenceNumberProcessor extends AbstractProcessor implements ExternalizableLite,
            PortableObject
    {

        /**
         * <p>The number of sequence numbers to generate (skip after generating the first).</p>
         */
        private long sequenceSize;

        /**
         * <p>The initial value of the sequence, if it does not exist.</p>
         */
        private long initialValue;


        /**
         * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
         */
        public GenerateSequenceNumberProcessor()
        {
            this.sequenceSize = 1;
            this.initialValue = 1;
        }


        /**
         * <p>Standard Constructor.</p>
         * 
         * @param initialValue Used iff the underlying sequence does not exist
         * @param sequenceSize The number of values to generate (allocate from the sequence)
         */
        public GenerateSequenceNumberProcessor(long initialValue,
                                               long sequenceSize)
        {
            this.initialValue = initialValue;
            this.sequenceSize = sequenceSize;
        }


        /**
         * {@inheritDoc}
         */
        public Object process(Entry entry)
        {

            State state;
            if (entry.isPresent())
            {
                state = (State) entry.getValue();
            }
            else
            {
                state = new State(initialValue);
            }

            long value = state.generate(sequenceSize);

            entry.setValue(state);

            return value;
        }


        /**
         * {@inheritDoc}
         */
        public void readExternal(DataInput in) throws IOException
        {
            this.initialValue = ExternalizableHelper.readLong(in);
            this.sequenceSize = ExternalizableHelper.readLong(in);
        }


        /**
         * {@inheritDoc}
         */
        public void writeExternal(DataOutput out) throws IOException
        {
            ExternalizableHelper.writeLong(out, initialValue);
            ExternalizableHelper.writeLong(out, sequenceSize);
        }


        /**
         * {@inheritDoc}
         */
        public void readExternal(PofReader reader) throws IOException
        {
            this.initialValue = reader.readLong(0);
            this.sequenceSize = reader.readLong(1);
        }


        /**
         * {@inheritDoc}
         */
        public void writeExternal(PofWriter writer) throws IOException
        {
            writer.writeLong(0, initialValue);
            writer.writeLong(1, sequenceSize);
        }
    }
}
