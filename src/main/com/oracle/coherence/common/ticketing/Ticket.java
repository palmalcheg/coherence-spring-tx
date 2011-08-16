/*
 * File: Ticket.java
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
package com.oracle.coherence.common.ticketing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.oracle.coherence.common.identifiers.Identifier;
import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link Ticket} is an specialized {@link Identifier} that represents a
 * ordered "sequence number" issued by an ordered "issuer".  That is, 
 * {@link Ticket}s are comparable and thus have a natural order.</p>
 *
 * <p>Internally {@link Ticket}s are represented by two "longs".  The first
 * "long" is used to identify the "issuer".  The second "long" is used to identify
 * the "sequence number" of the {@link Ticket} issued by the "issuer".</p>
 *
 * <p>As "issuers" are represented as longs, "issuers" are comparable.  Thus
 * {@link Ticket}s issued by issuer 0 will occur (be ordered) before 
 * {@link Ticket}s issued by issuer 1 etc.</p>
 * 
 * <p>A useful way to represent a range of {@link Ticket}s issued by a single 
 * "issuer" is to use a {@link TicketBook}.</p>
 * 
 * @see TicketBook
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Ticket implements Identifier, ExternalizableLite, PortableObject, Comparable<Ticket>
{
    /**
     * <p>A useful {@link Ticket} that may be used to represent "no" ticket.</p>
     */
    public static final Ticket NONE = new Ticket(-1, -1);

    /**
     * <p>The id of the "issuer" that issued the {@link Ticket}.</p>
     */
    private long issuerId;

    /**
     * <p>The sequence number of the {@link Ticket} issued by the issuer.</p>
     */
    private long sequenceNumber;


    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public Ticket()
    {
    }


    /**
     * <p>Used by the "issuer" to create {@link Ticket}s.</p>
     * 
     * @param issuerId The issuerId for the {@link Ticket}
     * @param sequenceNumber The sequenceNumber issued by the issuer
     */
    public Ticket(long issuerId,
                  long sequenceNumber)
    {
        this.issuerId = issuerId;
        this.sequenceNumber = sequenceNumber;
    }


    /**
     * <p>Returns the id of the "issuer" that issued the {@link Ticket}.</p>
     * 
     * @return The id of the "issuer" that issued the {@link Ticket}
     */
    public long getIssuerId()
    {
        return issuerId;
    }


    /**
     * <p>Returns the unique sequence number issued by the "issuer".</p>
     * 
     * @return The unique sequence number issued by the "issuer"
     */
    public long getSequenceNumber()
    {
        return sequenceNumber;
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        issuerId = ExternalizableHelper.readLong(in);
        sequenceNumber = ExternalizableHelper.readLong(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeLong(out, issuerId);
        ExternalizableHelper.writeLong(out, sequenceNumber);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.issuerId = reader.readLong(0);
        this.sequenceNumber = reader.readLong(1);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeLong(0, issuerId);
        writer.writeLong(1, sequenceNumber);
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (issuerId ^ (issuerId >>> 32));
        result = prime * result + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Ticket other = (Ticket) obj;
        if (issuerId != other.issuerId)
            return false;
        if (sequenceNumber != other.sequenceNumber)
            return false;
        return true;
    }


    /**
     * <p>A {@link Ticket} (x) occurs before (is less than) another {@link Ticket} (y) if
     * the issuerId of (x) is less than (y) or if the issuerIds are the same for (x) and (y), 
     * then the sequence number of (x) must be less than (y).</p>
     * 
     * {@inheritDoc}
     */
    public int compareTo(Ticket other)
    {
        if (this.getIssuerId() < other.getIssuerId())
            return -1;
        else if (this.getIssuerId() > other.getIssuerId())
            return +1;
        else if (this.getSequenceNumber() < other.getSequenceNumber())
            return -1;
        else if (this.getSequenceNumber() > other.getSequenceNumber())
            return +1;
        else
            return 0;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        if (this.equals(Ticket.NONE))
            return "Ticket.NONE";
        else
            return String.format("Ticket{%d.%d}", issuerId, sequenceNumber);
    }
}
