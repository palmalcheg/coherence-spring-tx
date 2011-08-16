/*
 * File: NullBackingMapListener.java
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
package com.oracle.coherence.common.backingmaplisteners;

import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

/**
 * <p>An implementation of a backing {@link MapListener} that does
 * nothing, including *not* deserializing the {@link MapEvent}s.</p>
 * 
 * <p>Instances of this class are often used in cache configurations
 * that require a Backing {@link MapListener}, but don't require
 * any actions to be performed.</p>
 * 
 * @author Brian Oliver
 */
public final class NullBackingMapListener implements MapListener
{

    /**
     * <p>Standard Constructor.</p>
     * 
     * @param backingMapManagerContext The BackingMapManagerContext associated with this listener
     */
    public NullBackingMapListener(BackingMapManagerContext backingMapManagerContext)
    {
        //SKIP: as a null implementation we don't do anything here ;)
    }

    /**
     * {@inheritDoc}
     */
    public void entryDeleted(MapEvent mapEvent)
    {
        //SKIP: as a null implementation we don't do anything here ;)
    }

    /**
     * {@inheritDoc}
     */
    public void entryInserted(MapEvent mapEvent)
    {
        //SKIP: as a null implementation we don't do anything here ;)
    }

    /**
     * {@inheritDoc}
     */
    public void entryUpdated(MapEvent mapEvent)
    {
        //SKIP: as a null implementation we don't do anything here ;)
    }
}
