/*
 * File: IntegerBasedIdentifier.java
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
 * <p>A {@link Integer}-based implementation of an {@link Identifier}.</p>
 * 
 * @author Christer Fahlgren
 */
@SuppressWarnings("serial")
public class IntegerBasedIdentifier implements Identifier, ExternalizableLite, PortableObject
{

    /**
     * <p>The Integer being used as an {@link Identifier}.</p>
     */
    private int value;


    /**
     * <p>A factory method to create {@link Integer}-based {@link Identifier}s.</p>
     * 
     * @param value the {@link Integer} to use as an identifier
     * 
     * @return a {@link Integer}-based {@link Identifier}.
     */
    public static Identifier newInstance(int value)
    {
        return new IntegerBasedIdentifier(value);
    }


    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public IntegerBasedIdentifier()
    {
    }


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param value the {@link Integer} to use as an identifier
     */
    private IntegerBasedIdentifier(int value)
    {
        this.value = value;
    }


    /**
     * <p>Returns the internal value used for the identifier.</p>
     * 
     * @return the internal value used for the identifier
     */
    public int getint()
    {
        return value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
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
        IntegerBasedIdentifier other = (IntegerBasedIdentifier) obj;
        if (value != other.value)
            return false;
        return true;
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
    public void readExternal(DataInput in) throws IOException
    {
        this.value = ExternalizableHelper.readInt(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeInt(out, value);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.value = reader.readInt(0);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeInt(0, value);
    }
}
