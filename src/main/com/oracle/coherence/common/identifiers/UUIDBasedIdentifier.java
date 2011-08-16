/*
 * File: UUIDBasedIdentifier.java
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
import com.tangosol.util.UUID;

/**
 * <p>A {@link UUID}-based implementation of an {@link Identifier}.</p>
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class UUIDBasedIdentifier implements Identifier, ExternalizableLite, PortableObject
{

    /**
     * <p>The {@link UUID} being used as an {@link Identifier}.</p>
     */
    private UUID uuid;

    /**
     * <p>A factory method to create {@link UUID}-based {@link Identifier}s.</p>
     * 
     * @return a {@link UUID}-based {@link Identifier}s.
     */
    public static Identifier newInstance()
    {
        return new UUIDBasedIdentifier(new UUID());
    }

    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public UUIDBasedIdentifier()
    {
    }

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param uuid the {@link UUID} to use
     */
    private UUIDBasedIdentifier(UUID uuid)
    {
        this.uuid = uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
        final UUIDBasedIdentifier other = (UUIDBasedIdentifier) obj;
        if (uuid == null)
        {
            if (other.uuid != null)
                return false;
        }
        else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    /**
     * <p>Returns the internal value used for the identifier.</p>
     * 
     * @return the internal value used for the identifier
     */
    public UUID getUUID()
    {
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("Identifier{%s}", uuid);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.uuid = (UUID) ExternalizableHelper.readExternalizableLite(in);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeExternalizableLite(out, uuid);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.uuid = (UUID) reader.readObject(0);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeObject(0, uuid);
    }
}
