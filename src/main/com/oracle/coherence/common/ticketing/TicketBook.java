/*
 * File: TicketBook.java
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
import java.util.Iterator;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link TicketBook} provides an efficient mechanism for representing
 * a non-thread-safe and mutable comparable collections of monotonically 
 * increasing {@link Ticket}s.</p>
 * 
 * @see Ticket
 * @see TicketAggregator
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class TicketBook implements Comparable<TicketBook>, Iterable<Ticket>, ExternalizableLite, PortableObject
{
    /**
     * <p>The issuer of the {@link Ticket}s represented by the {@link TicketBook}.</p>
     */
    private long issuerId;

    /**
     * <p>The start of the range of {@link Ticket} sequence numbers (inclusive).</p>
     */
    private long from;

    /**
     * <p>The end of the range of {@link Ticket} sequence numbers (inclusive).</p>
     */
    private long to;


    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public TicketBook()
    {
    }


    /**
     * <p>The Standard Constructor (for an empty {@link TicketBook}).</p>
     * 
     * @param issuerId The issuer identifier for the new {@link TicketBook}.
     */
    public TicketBook(long issuerId)
    {
        this.issuerId = issuerId;
        this.from = 1;
        this.to = 0;
    }


    /**
     * <p>The Standard Constructor for a {@link TicketBook} with a specific range of values.</p>
     * 
     * @param issuerId The issuer identifier for the new {@link TicketBook}.
     * @param from The starting value of the {@link TicketBook}.
     * @param to The last value of the {@link TicketBook}.
     */
    public TicketBook(long issuerId,
                      long from,
                      long to)
    {
        this.issuerId = issuerId;
        this.from = from;
        this.to = to;
    }


    /**
     * <p>The Standard Constructor for a {@link TicketBook} with an initial {@link Ticket}.</p>
     * 
     * @param ticket The initial {@link Ticket}.
     */
    public TicketBook(Ticket ticket)
    {
        this.issuerId = ticket.getIssuerId();
        this.from = ticket.getSequenceNumber();
        this.to = ticket.getSequenceNumber();
    }


    /**
     * <p>Returns the Id of the issuer that is responsible for the 
     * {@link Ticket}s represented by the {@link TicketBook}.</p>
     * 
     * @return The issuer identifier for the {@link TicketBook}
     */
    public long getIssuerId()
    {
        return issuerId;
    }


    /**
     * <p>Returns if the {@link TicketBook} is empty (does not
     * contain any {@link Ticket}s).</p>
     * 
     * @return <code>true</code> if the {@link TicketBook} is empty.
     */
    public boolean isEmpty()
    {
        return from > to;
    }


    /**
     * <p>Returns the id of the first {@link Ticket} in the {@link TicketBook}.</p>
     * 
     * @return The identifier of the first {@link Ticket} in the {@link TicketBook}
     */
    public long getFrom()
    {
        return from;
    }


    /**
     * <p>Returns the id of the last {@link Ticket} in the {@link TicketBook}.</p>
     * 
     * @return The identifier of the last {@link Ticket} in the {@link TicketBook}
     */
    public long getTo()
    {
        return to;
    }


    /**
     * <p>Returns the number of {@link Ticket}s in the {@link TicketBook}.</p>
     * 
     * @return The number of {@link Ticket}s in the {@link TicketBook}.
     */
    public long size()
    {
        return isEmpty() ? 0 : to - from + 1;
    }


    /**
     * <p>Extends a {@link TicketBook} range of {@link Ticket}s by 1
     * and returns the new {@link Ticket} created.</p>
     * 
     * @return The new {@link Ticket} created as a result of extending the {@link TicketBook}.
     */
    public Ticket extend()
    {
        return new Ticket(issuerId, ++to);
    }


    /**
     * <p>Returns if the specified {@link Ticket} is contained with in the {@link TicketBook}.</p>
     * 
     * @param ticket The {@link Ticket} being checked.
     * 
     * @return <code>true</code> If the specified {@link Ticket} is in the {@link TicketBook}.
     */
    public boolean containsTicket(Ticket ticket)
    {
        return !isEmpty() && ticket.getIssuerId() == issuerId && ticket.getSequenceNumber() >= from
                && ticket.getSequenceNumber() <= to;
    }


    /**
     * <p>Returns if the specified {@link Ticket} is adjacent to the range of {@link Ticket}s
     * represented by this {@link TicketBook}.</p>
     * 
     * @param ticket The {@link Ticket} being checked.
     * 
     * @return <code>true</code> if the specified {@link Ticket} is adjacent to the range of {@link Ticket}s
     *         represented by this {@link TicketBook}.
     */
    public boolean isAdjacent(Ticket ticket)
    {
        return ticket.getIssuerId() == issuerId
                && (ticket.getSequenceNumber() == from - 1 || ticket.getSequenceNumber() == to + 1);
    }


    /**
     * <p>Returns if the specified {@link TicketBook} is adjacent to the range of {@link Ticket}s
     * represented by this {@link TicketBook}.</p>
     * 
     * @param ticketBook The {@link TicketBook} being checked.
     * 
     * @return <code>true</code> if the specified {@link TicketBook} is adjacent to the range of {@link Ticket}s
     *         represented by this {@link TicketBook}.
     */
    public boolean isAdjacent(TicketBook ticketBook)
    {
        return ticketBook.getIssuerId() == issuerId
                && (ticketBook.getTo() == from - 1 || ticketBook.getFrom() == to + 1);
    }


    /**
     * <p>Returns if the specified {@link TicketBook} has tickets that are contained within this
     * {@link TicketBook}.</p>
     * 
     * @param ticketBook The {@link TicketBook} being checked.
     * 
     * @return <code>true</code> if the specified {@link TicketBook} has tickets that are contained within this
     *         {@link TicketBook}.
     */
    public boolean intersects(TicketBook ticketBook)
    {
        return ticketBook.getIssuerId() == issuerId
                && ((ticketBook.getFrom() >= from && ticketBook.getFrom() <= to) || (ticketBook.getTo() >= from && ticketBook
                    .getTo() <= to));
    }


    /**
     * <p>Attempts to add the specified {@link Ticket} to the {@link TicketBook} and
     * returns if it was successful.  A {@link Ticket} may only be added to a {@link Ticket}
     * book if and only if;</p>
     * <ol>
     *      <li>The {@link Ticket} is already contained in this {@link TicketBook} or </li>
     *      <li>The {@link Ticket} is adjacent to this the range of {@link Ticket}s in this {@link TicketBook}.</li>
     * </ol>
     * 
     * @param ticket The {@link Ticket} to add.
     * 
     * @return <code>true</code> If the {@link Ticket} was successfully added.
     */
    public boolean add(Ticket ticket)
    {
        if (this.containsTicket(ticket))
        {
            return true;
        }
        else if (this.isAdjacent(ticket))
        {
            if (ticket.getSequenceNumber() == from - 1)
                from = from - 1;
            else
                to = to + 1;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * <p>Attempts to combine all of the {@link Ticket}s in the specified {@link TicketBook} to
     * this {@link TicketBook} and returns if it was successful. A {@link TicketBook} may 
     * only be added another {@link TicketBook} if and only if;</p>
     * <ol>
     *      <li>The specified {@link TicketBook} is intersecting this {@link TicketBook} or </li>
     *      <li>The specified {@link TicketBook} is adjacent to this {@link TicketBook}.</li>
     * </ol>
     * 
     * @param ticketBook The {@link TicketBook} being combined.
     * 
     * @return <code>true</code> If the combination was successful.
     */
    public boolean combine(TicketBook ticketBook)
    {
        if (this.intersects(ticketBook) || this.isAdjacent(ticketBook))
        {
            from = Math.min(from, ticketBook.getFrom());
            to = Math.max(to, ticketBook.getTo());
            return true;
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
        return String.format("TicketBook{issuerId=%d, from=%d, to=%d}", issuerId, from, to);
    }


    /**
     * {@inheritDoc}
     */
    public int compareTo(TicketBook other)
    {
        if (this.getIssuerId() < other.getIssuerId())
            return -1;
        else if (this.getIssuerId() > other.getIssuerId())
            return +1;
        else if (this.from < other.from)
            return -1;
        else if (this.from > other.from)
            return +1;
        else if (this.to < other.to)
            return -1;
        else if (this.to > other.to)
            return +1;
        else
            return 0;
    }


    /**
     * <p>Removes and returns an {@link Iterator} over the first n 
     * {@link Ticket}s from the {@link TicketBook}.</p>  
     * 
     * <p>After calling this method there will be n less {@link Ticket}s 
     * in the {@link TicketBook}. If the {@link #size()} of the {@link TicketBook} 
     * is less than n, then {@link #size()} {@link Ticket}s will be issued.</p>
     * 
     * @param n The number of {@link Ticket}s to issue from the {@link Ticket}.
     * 
     * @return An {@link Iterator} of {@link Ticket}s issued from the {@link TicketBook}.
     */
    public Iterator<Ticket> issue(long n)
    {
        Iterator<Ticket> iterator;

        if (isEmpty() || n <= 0)
        {
            iterator = new TicketIterator(issuerId, from, from - 1);
        }
        if (size() < n)
        {
            iterator = new TicketIterator(issuerId, from, to);
            from = to + 1;
        }
        else
        {
            iterator = new TicketIterator(issuerId, from, from + n - 1);
            from = from + n;
        }
        return iterator;
    }


    /**
     * <p>Returns an {@link Iterator} over the first n {@link Ticket}s from the {@link TicketBook}.</p>  
     * 
     * <p>If the {@link #size()} of the {@link TicketBook} 
     * is less than n, then an {@link Iterator} over the first {@link #size()} {
     * @link Ticket}s will be issued.</p>
     * 
     * @param n The number of {@link Ticket}s to iterate over.
     * 
     * @return An {@link Iterator} of the first n {@link Ticket}s from the {@link TicketBook}.
     */
    public Iterator<Ticket> first(long n)
    {
        Iterator<Ticket> iterator;

        if (isEmpty() || n <= 0)
        {
            iterator = new TicketIterator(issuerId, from, from - 1);
        }
        if (size() < n)
        {
            iterator = new TicketIterator(issuerId, from, to);
        }
        else
        {
            iterator = new TicketIterator(issuerId, from, from + n - 1);
        }
        return iterator;
    }


    /**
     * <p>Removes the first n {@link Ticket}s from the {@link TicketBook}.</p>  
     * 
     * <p>If the {@link #size()} of the {@link TicketBook} is less than n, 
     * then {@link #size()} {@link Ticket}s will be removed.</p>
     * 
     * @param n The number of {@link Ticket}s to remove from the {@link TicketBook}.
     */
    public void consume(long n)
    {
        if (isEmpty() || n <= 0)
        {
            //do nothing
        }
        if (size() < n)
        {
            from = to + 1;
        }
        else
        {
            from = from + n;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Ticket> iterator()
    {
        return new TicketIterator(issuerId, from, to);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.issuerId = ExternalizableHelper.readLong(in);
        this.from = ExternalizableHelper.readLong(in);
        this.to = ExternalizableHelper.readLong(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeLong(out, issuerId);
        ExternalizableHelper.writeLong(out, from);
        ExternalizableHelper.writeLong(out, to);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.issuerId = reader.readLong(0);
        this.from = reader.readLong(1);
        this.to = reader.readLong(2);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeLong(0, issuerId);
        writer.writeLong(1, from);
        writer.writeLong(2, to);
    }

    /**
     * <p>An {@link Iterator} that is used to iterate over {@link Ticket}s 
     * from a {@link TicketBook}.</p>
     */
    private static class TicketIterator implements Iterator<Ticket>
    {
        /**
         * <p>The issuerId of the {@link Ticket}s that will be iterated over.</p>
         */
        private long issuerId;

        /**
         * <p>The first of the {@link Ticket}s to iterate.</p>
         */
        private long start;

        /**
         * <p>The next {@link Ticket}s to return from the iteration.</p>
         */
        private long next;
        
        /**
         * <p>The last of the {@link Ticket}s to iterate.</p>
         */
        private long last;


        /**
         * <p>Standard Constructor.</p>
         * 
         * @param issuerId The issuerId for the {@link Ticket}s being iterated.
         * @param start The starting {@link Ticket} of the iteration. 
         * @param last The last {@link Ticket} in the iteration.
         */
        public TicketIterator(long issuerId,
                              long start,
                              long last)
        {
            this.issuerId = issuerId;
            this.start = start;
            this.next = start;
            this.last = last;
        }


        /**
         * {@inheritDoc}
         */
        public boolean hasNext()
        {
            return next <= last;
        }


        /**
         * {@inheritDoc}
         */
        public Ticket next()
        {
            return new Ticket(issuerId, next++);
        }


        /**
         * {@inheritDoc}
         */
        public void remove()
        {
            throw new UnsupportedOperationException("Unable to remove() tickets from a TicketIterator");
        }


        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return String
                .format("TicketIterator{issuerId=%d, start=%d, next=%d, last=%d}", issuerId, start, next, last);
        }
    }
}
