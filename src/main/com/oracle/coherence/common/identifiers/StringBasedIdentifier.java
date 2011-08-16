/*
 * File: StringBasedIdentifier.java
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
 * <p>A {@link String}-based implementation of an {@link Identifier}.</p>
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class StringBasedIdentifier implements Identifier, ExternalizableLite, PortableObject
{

    /**
     * <p>The string being used as an {@link Identifier}.</p>
     */
    private String string;

    /**
     * <p>A factory method to create {@link String}-based {@link Identifier}s.</p>
     * 
     * @param string the {@link String} to use as an identifier
     * 
     * @return a {@link String}-based {@link Identifier}.
     */
    public static Identifier newInstance(String string)
    {
        return new StringBasedIdentifier(string);
    }

    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public StringBasedIdentifier()
    {
    }

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param string the {@link String} to use as an identifier
     */
    private StringBasedIdentifier(String string)
    {
        this.string = string;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((string == null) ? 0 : string.hashCode());
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
        final StringBasedIdentifier other = (StringBasedIdentifier) obj;
        if (string == null)
        {
            if (other.string != null)
                return false;
        }
        else if (!string.equals(other.string))
            return false;
        return true;
    }

    /**
     * <p>Returns the internal value used for the identifier.</p>
     * 
     * @return the internal value used for the identifier
     */
    public String getString()
    {
        return string;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("Identifier{%s}", string);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.string = ExternalizableHelper.readSafeUTF(in);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeSafeUTF(out, string);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.string = reader.readString(0);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeString(0, string);
    }
}
