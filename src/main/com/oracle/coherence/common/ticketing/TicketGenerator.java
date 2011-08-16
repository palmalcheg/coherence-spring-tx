/*
 * File: TicketGenerator.java
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

/**
 * <p>A {@link TicketGenerator} may be used to generate unique {@link Ticket}s.</p>
 * 
 * <p>NOTE: The "uniqueness" of the {@link Ticket}s generated is implementation dependent.</p>
 * 
 * @author Brian Oliver
 */
public interface TicketGenerator
{
    /**
     * <p>Returns a unique {@link Ticket} from the {@link TicketGenerator}.</p>
     * 
     * @return A unique {@link Ticket} from the {@link TicketGenerator}.
     */
    public Ticket next();
}
