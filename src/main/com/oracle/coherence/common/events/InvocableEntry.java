/*
 * File: InvocableEntry.java
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

import java.util.Map;

import com.tangosol.util.InvocableMap.EntryProcessor;

/**
 * <p>An {@link InvocableEntry} is an {@link java.util.Map.Entry} that supports execution of
 * {@link EntryProcessor}s, without explicitly requiring a reference to an
 * underlying {@link com.tangosol.net.NamedCache} and/or {@link com.tangosol.util.InvocableMap}.</p>
 * 
 * @see com.tangosol.util.InvocableMap.EntryProcessor
 * @see com.tangosol.util.InvocableMap
 * 
 * @author Brian Oliver
 */
@SuppressWarnings("unchecked")
public interface InvocableEntry extends Map.Entry
{
    /**
     * <p>Invokes the specified {@link EntryProcessor} against the
     * {@link InvocableEntry} returning the result of the invocation.</p>
     * 
     * @param entryProcessor The {@link EntryProcessor} to invoke.
     * 
     * @return the result of invoking the EntryProcessor
     */
    public Object invoke(EntryProcessor entryProcessor);
}
