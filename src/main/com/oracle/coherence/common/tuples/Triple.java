/*
 * File: Triple.java
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

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>An immutable sequence of three type-safe serializable values.</p>
 * 
 * @param <X> The type of the first value of the {@link Triple}
 * @param <Y> The type of the second value of the {@link Triple}
 * @param <Z> The type of the third value of the {@link Triple}
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Triple<X, Y, Z> implements Tuple
{
    /**
     * <p>The first value of the {@link Triple}.</p>
     */
    private X x;

    /**
     * <p>The second value of the {@link Triple}.</p>
     */
    private Y y;

    /**
     * <p>The third value of the {@link Triple}.</p>
     */
    private Z z;


    /**
     * <p>Required for ExternalizableLite and PortableObject.</p>
     */
    public Triple()
    {
        //for ExternalizableLite
    }


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param x The first value of the {@link Triple}
     * @param y The second value of the {@link Triple}
     * @param z The third value of the {@link Triple} 
     */
    public Triple(X x,
                  Y y,
                  Z z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
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
        else if (index == 2)
            return z;
        else
            throw new IndexOutOfBoundsException(String.format("%d is an illegal index for a Triple", index));
    }


    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return 3;
    }


    /**
     * <p>Returns the first value of the {@link Triple}.</p>
     * 
     * @return The first value of the {@link Triple}
     */
    public X getX()
    {
        return x;
    }


    /**
     * <p>Returns the second value of the {@link Triple}.</p>
     * 
     * @return The second value of the {@link Triple}
     */
    public Y getY()
    {
        return y;
    }


    /**
     * <p>Returns the third value of the {@link Triple}.</p>
     * 
     * @return The third value of the {@link Triple}
     */
    public Z getZ()
    {
        return z;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(DataInput in) throws IOException
    {
        this.x = (X) ExternalizableHelper.readObject(in);
        this.y = (Y) ExternalizableHelper.readObject(in);
        this.z = (Z) ExternalizableHelper.readObject(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeObject(out, x);
        ExternalizableHelper.writeObject(out, y);
        ExternalizableHelper.writeObject(out, z);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(PofReader reader) throws IOException
    {
        x = (X) reader.readObject(0);
        y = (Y) reader.readObject(1);
        z = (Z) reader.readObject(2);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeObject(0, x);
        writer.writeObject(1, y);
        writer.writeObject(2, z);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("Triple<%s, %s, %s>", x == null ? "null" : x.toString(),
            y == null ? "null" : y.toString(), z == null ? "null" : z.toString());
    }
}
