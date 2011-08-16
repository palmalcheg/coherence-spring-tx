/*
 * File: Event.java
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
package com.oracle.coherence.common.events;

/**
 * <p>An {@link Event} object captures the necessary information required to 
 * adequately describe some activity that has occurred, of which may be processed
 * at some later point in time.</p>
 * 
 * <p>This interface is common to all {@link Event}s raised by the Coherence Incubator
 * projects.</p>  
 * 
 * @author Brian Oliver
 */
public interface Event
{

    /**
     * <p>This is a marker interface for all Coherence Incubator Events.</p>
     */
}
