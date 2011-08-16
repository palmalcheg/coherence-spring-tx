/*
 * File: LongBasedIdentifier.java
 * 
 * Copyright (c) 2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.identifiers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link Long}-based implementation of an {@link Identifier}.</p>
 * 
 * @author Christer Fahlgren
 */
@SuppressWarnings("serial")
public class LongBasedIdentifier implements Identifier, ExternalizableLite, PortableObject
{

    /**
     * <p>The Long being used as an {@link Identifier}.</p>
     */
    private long value;


    /**
     * <p>A factory method to create {@link Long}-based {@link Identifier}s.</p>
     * 
     * @param value the {@link Long} to use as an identifier
     * 
     * @return a {@link Long}-based {@link Identifier}.
     */
    public static Identifier newInstance(long value)
    {
        return new LongBasedIdentifier(value);
    }


    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public LongBasedIdentifier()
    {
    }


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param value the {@link Long} to use as an identifier
     */
    private LongBasedIdentifier(long value)
    {
        this.value = value;
    }


    /**
     * <p>Returns the internal value used for the identifier.</p>
     * 
     * @return the internal value used for the identifier
     */
    public long getlong()
    {
        return value;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("Identifier{%d}", value);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (value ^ (value >>> 32));
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LongBasedIdentifier other = (LongBasedIdentifier) obj;
        if (value != other.value)
            return false;
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.value = ExternalizableHelper.readLong(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeLong(out, value);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.value = reader.readLong(0);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeLong(0, value);
    }
}
