/*
 * File: DefaultTicketGenerator.java
 * 
 * Copyright (c) 2008. All Rights Reserved. Oracle Corporation.
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

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>A default implementation of a {@link TicketGenerator} (uniqueness
 * of the generated {@link Ticket}s dependent on the uniqueness of issuerId 
 * provided at construction).</p>
 * 
 * @see Ticket
 * 
 * @author Brian Oliver
 */
public class DefaultTicketGenerator implements TicketGenerator
{
    /**
     * <p>The issuerId for the generated {@link Ticket}s.</p>
     */
    private long issuerId;

    /**
     * <p>An {@link AtomicLong} for the sequence numbers used
     * to generate {@link Ticket}s.</p>
     */
    private AtomicLong nextSequenceNr;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param issuerId The issuerId of the {@link TicketGenerator}
     */
    public DefaultTicketGenerator(long issuerId)
    {
        this.issuerId = issuerId;
        this.nextSequenceNr = new AtomicLong(1);
    }


    /**
     * {@inheritDoc}
     */
    public Ticket next()
    {
        return new Ticket(issuerId, nextSequenceNr.getAndIncrement());
    }
}
