/*
 * File: Pair.java
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>An immutable sequence of two type-safe serializable values.</p>
 * 
 * @param <X> The type of the first value of the {@link Pair}
 * @param <Y> The type of the second value of the {@link Pair}
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Pair<X, Y> implements Tuple
{

    /**
     * <p>The first value of the {@link Pair}.</p>
     */
    private X x;

    /**
     * <p>The second value of the {@link Pair}.</p>
     */
    private Y y;


    /**
     * <p>Required for ExternalizableLite and PortableObject.</p>
     */
    public Pair()
    {
    }


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param x The first value of the {@link Pair}
     * @param y The second value of the {@link Pair}
     */
    public Pair(X x,
                Y y)
    {
        this.x = x;
        this.y = y;
    }


    /**
     * <p>Standard Constructor to create a {@link Pair} based on a Map.Entry.</p>
     * 
     * @param entry A Map.Entry from which to create a {@link Pair}
     */
    public Pair(Map.Entry<X, Y> entry)
    {
        this.x = entry.getKey();
        this.y = entry.getValue();
    }


    /**
     * {@inheritDoc}
     */
    public Object get(int index) throws IndexOutOfBoundsException
    {
        if (index == 0)
            return x;
        else if (index == 1)
            return y;
        else
            throw new IndexOutOfBoundsException(String.format("%d is an illegal index for a Pair", index));
    }


    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return 2;
    }


    /**
     * <p>Returns the first value of the {@link Pair}.</p>
     * 
     * @return The first value of the {@link Pair}
     */
    public X getX()
    {
        return x;
    }


    /**
     * <p>Returns the second value of the {@link Pair}.</p>
     * 
     * @return The second value of the {@link Pair}
     */
    public Y getY()
    {
        return y;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(DataInput in) throws IOException
    {
        this.x = (X) ExternalizableHelper.readObject(in);
        this.y = (Y) ExternalizableHelper.readObject(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeObject(out, x);
        ExternalizableHelper.writeObject(out, y);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(PofReader reader) throws IOException
    {
        this.x = (X) reader.readObject(0);
        this.y = (Y) reader.readObject(1);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeObject(0, x);
        writer.writeObject(1, y);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("Pair<%s, %s>", x == null ? "null" : x.toString(), y == null ? "null" : y.toString());
    }
}
