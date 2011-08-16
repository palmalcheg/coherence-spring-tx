/*
 * File: LifecycleAwareEntry.java
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
package com.oracle.coherence.common.events.processing;

import com.oracle.coherence.common.events.EntryEvent;
import com.oracle.coherence.common.events.Event;

/**
 * <p>A {@link LifecycleAwareEntry} is a cache {@link java.util.Map.Entry} that is capable of handling
 * {@link com.oracle.coherence.common.events.Event}s that occur on itself.</p>
 * 
 * <p>Essentially a {@link LifecycleAwareEntry} is a specialized {@link EventProcessor},
 * specifically for {@link EntryEvent}s.</p>
 * 
 * <p>Unlike the original LifecycleAwareCacheEntry class from Coherence Common 1.5 which where based on 
 * {@link com.tangosol.util.MapEvent}s, {@link LifecycleAwareEntry}s are based on the new {@link EntryEvent}
 * interfaces/classes.</p>
 * 
 * <p>Note however, like Coherence Common 1.5, processing of the {@link Event} occurs on the thread that raised
 * the said {@link Event} and as such care should be taken it the {@link Event} was raised by a Coherence owned 
 * Service/Worker thread.  If there is any doubt as to the thread-safety of calls made by the 
 * {@link EventProcessor#process(com.oracle.coherence.common.events.dispatching.EventDispatcher, Event)} method, 
 * the said method should use an alternate thread for execution.</p>
 * 
 * <p><strong>Advice:</strong> It's generally recommended that you implement the {@link EventProcessor}
 * interface instead of this interface as it's possibly clearer for the reader of your Entry/Value object.</p>
 * 
 * @author Brian Oliver
 */
public interface LifecycleAwareEntry extends EventProcessor<EntryEvent>
{
    /**
     * <p>This interface is deliberately empty as it inherits the signatures of the EventProcessor.</p>
     */
}
