/*
 * File: TicketAggregator.java
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

import com.tangosol.util.ValueExtractor;
import com.tangosol.util.aggregator.AbstractAggregator;

/**
 * <p>The {@link TicketAggregator} is a parallel {@link AbstractAggregator} that
 * produces an ordered {@link LinkedList} of {@link TicketBook}s (based on natural ordering
 * of {@link TicketBook}s).  Each {@link TicketBook} optimally represents an ordered 
 * range of {@link Ticket}s that have been aggregated for each "issuer".</p>
 * 
 * <p>This aggregator is useful for constructing ranges of {@link Ticket}s from across
 * a cluster.</p>
 * 
 * <p>This aggregator has two passes.  The first pass (non-final) collects all of the {@link Ticket}s
 * for each "issuer" to produce a set of {@link TicketBook}s.  The second pass (final)
 * combines the {@link TicketBook}s and produces a {@link LinkedList} of {@link TicketBook}s,
 * representing all of the {@link Ticket}s that have been aggregated.</p>
 * 
 * <p>When using this aggregator constructor, you must specify either the name of the method 
 * that returns a {@link Ticket} OR a {@link ValueExtractor} that produces a {@link Ticket}
 * to be aggregated.</p>
 * 
 * @see Ticket
 * @see TicketBook
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class TicketAggregator extends AbstractAggregator
{
    /**
     * <p>A map of ordered {@link Ticket}s by issuer id.
     * This is used for the "first pass" of the aggregation
     * to organize {@link Ticket}s into {@link TicketBook}s.</p>
     */
    private transient TreeMap<Long, TreeSet<Ticket>> ticketsByIssuer;

    /**
     * <p>An ordered set of {@link TicketBook}s.  This is 
     * used for the "second pass" of the aggregation 
     * to organize and collapse the collected {@link TicketBook}s.</p>
     */
    private transient TreeSet<TicketBook> ticketBooks;


    /**
     * <p>Required for ExternalizableLite and/or PortableObject.</p>
     */
    public TicketAggregator()
    {
        super();
    }


    /**
     * <p>Standard Constructor (when providing the name of the method that returns 
     * a {@link Ticket} for aggregation).</p>
     * 
     * @param methodName The name of the method to use to extract a {@link Ticket} for aggregation
     */
    public TicketAggregator(String methodName)
    {
        super(methodName);
    }


    /**
     * <p>Standard Constructor (when providing a {@link ValueExtractor} that returns 
     * a {@link Ticket} for aggregation).</p>
     * 
     * @param extractor The {@link ValueExtractor} to extract a {@link Ticket} for aggregation
     */
    public TicketAggregator(ValueExtractor extractor)
    {
        super(extractor);
    }


    /**
     * {@inheritDoc}
     */
    protected void init(boolean isFinal)
    {
        if (isFinal)
        {
            this.ticketBooks = new TreeSet<TicketBook>();
        }
        else
        {
            this.ticketsByIssuer = new TreeMap<Long, TreeSet<Ticket>>();
        }
    }


    /**
     * {@inheritDoc}
     */
    protected Object finalizeResult(boolean isFinal)
    {
        if (isFinal)
        {
            LinkedList<TicketBook> result = new LinkedList<TicketBook>();
            TicketBook ticketBook = null;
            for (TicketBook aTicketBook : ticketBooks)
            {
                if (ticketBook == null || !ticketBook.combine(aTicketBook))
                {
                    ticketBook = aTicketBook;
                    result.add(ticketBook);
                }
            }
            return result;

        }
        else
        {
            TreeSet<TicketBook> result = new TreeSet<TicketBook>();

            for (long issuerId : ticketsByIssuer.keySet())
            {
                TicketBook ticketBook = null;
                for (Ticket ticket : ticketsByIssuer.get(issuerId))
                {
                    if (ticketBook == null || !ticketBook.add(ticket))
                    {
                        ticketBook = new TicketBook(ticket);
                        result.add(ticketBook);
                    }
                }
            }
            return result;
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected void process(Object object,
                           boolean isFinal)
    {
        if (isFinal)
        {
            ticketBooks.addAll((Collection<TicketBook>) object);

        }
        else
        {
            if (object instanceof Ticket)
            {
                Ticket ticket = (Ticket) object;

                TreeSet<Ticket> tickets;
                if (ticketsByIssuer.containsKey(ticket.getIssuerId()))
                {
                    tickets = ticketsByIssuer.get(ticket.getIssuerId());
                }
                else
                {
                    tickets = new TreeSet<Ticket>();
                    ticketsByIssuer.put(ticket.getIssuerId(), tickets);
                }

                tickets.add(ticket);
            }
            else
            {
                //we can't aggregate the object as it's not a Ticket
            }
        }
    }
}
