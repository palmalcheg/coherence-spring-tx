/*
 * File: LifecycleEventFilter.java
 * 
 * Copyright (c) 2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.environment.extensible;

import com.oracle.coherence.common.events.lifecycle.LifecycleEvent;
import com.tangosol.util.Filter;

/**
 * <p>A {@link LifecycleEventFilter} </p>
 * 
 * <p>NOTE: {@link LifecycleEventFilter}s are not serializable as they 
 * are only ever used locally.</p>
 * 
 * @author Brian Oliver
 */
public class LifecycleEventFilter implements Filter
{
    /**
     * <p>The static instance of a {@link LifecycleEventFilter}.</p>
     */
    public static final LifecycleEventFilter INSTANCE = new LifecycleEventFilter();


    /**
     * {@inheritDoc}
     */
    public boolean evaluate(Object object)
    {
        return object != null && object instanceof LifecycleEvent<?>;
    }
}
