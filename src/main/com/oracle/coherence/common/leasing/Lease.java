/*
 * File: Lease.java
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
package com.oracle.coherence.common.leasing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link Lease} represents the mutable conditions (typically time) 
 * that the owner of the said {@link Lease} may use an entity.</p>
 *  
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Lease implements ExternalizableLite, PortableObject
{
    /**
     * <p>The {@link Logger} for the class.</p>
     */
    private static final Logger logger = Logger.getLogger(Lease.class.getName());

    /**
     * <p>The instant in time (since the epoc) when the {@link Lease}
     * was acquired.</p>
     */
    private long acquisitionTime;

    /**
     * <p>The instant in time (since te epoc) when the {@link Lease}
     * was last updated.</p>
     */
    private long lastUpdateTime;

    /**
     * <p>The duration for which the {@link Lease} will be valid
     * (from the last update time).</p>
     */
    private long duration;


    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public Lease()
    {
        this.acquisitionTime = 0;
        this.lastUpdateTime = 0;
        this.duration = 0;
    }


    /**
     * <p>Package Level Constructor to create a {@link Lease} from the
     * current time for a specified duration (in milliseconds).</p>
     * 
     * @param duration The duration of the {@link Lease} in milliseconds.
     */
    Lease(long duration)
    {
        this.acquisitionTime = System.currentTimeMillis();
        this.lastUpdateTime = this.acquisitionTime;
        this.duration = duration;
    }


    /**
     * <p>Package Level Constructor to create a {@link Lease} with a
     * specified time of acquisition and duration.</p>
     * 
     * @param acqusitionTime The time (in milliseconds since the epoc) when the lease was acquired.
     * @param duration The duration of the {@link Lease} in milliseconds.
     */
    Lease(long acqusitionTime,
          long duration)
    {
        this.acquisitionTime = acqusitionTime;
        this.lastUpdateTime = acqusitionTime;
        this.duration = duration;
    }


    /**
     * <p>Returns the time (since the epoc in milliseconds) since the {@link Lease} was initially acquired.</p>
     * 
     * @return the time (since the epoc in milliseconds) since the {@link Lease} was initially acquired
     */
    public long getAcquisitionTime()
    {
        return acquisitionTime;
    }


    /**
     * <p>Returns the time (since the epoc in milliseconds) since the {@link Lease} was last updated.</p>
     * 
     * @return the time (since the epoc in milliseconds) since the {@link Lease} was last updated
     */
    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }


    /**
     * <p>Returns the duration (in milliseconds) that the {@link Lease} is valid (after the last update time).</p>
     * 
     * @return the duration (in milliseconds) that the {@link Lease} is valid (after the last update time).
     */
    public long getDuration()
    {
        return duration;
    }


    /**
     * <p>Returns if the {@link Lease} has been canceled.</p>
     * 
     * @return if the {@link Lease} has been canceled
     */
    public boolean isCanceled()
    {
        return duration == -1;
    }


    /**
     * <p>Attempts to cancel the ownership of the {@link Lease}.</p> 
     * 
     * <p>NOTE: If the {@link Lease} has previously been canceled
     * this method does nothing.</p>
     * 
     * @param cancel the value to set
     */
    public void setIsCanceled(boolean cancel)
    {
        if (!isCanceled() && cancel)
            this.lastUpdateTime = System.currentTimeMillis();
        this.duration = -1;
    }


    /**
     * <p>Returns if the {@link Lease} is suspended.</p>
     * 
     * <p>NOTE: Unlike canceled {@link Lease}s, suspended {@link Lease}s may 
     * be revived (extended)</p>
     * 
     * @return if the {@link Lease} is suspended
     */
    public boolean isSuspended()
    {
        return duration == -2;
    }


    /**
     * <p>Sets that the {@link Lease} ownership has been suspended
     * (but it may be recovered again at some later point in time).</p>
     * 
     * <p>NOTE: to unsuspend a {@link Lease} you should simply extend it.</p>
     * 
     * @param suspend the value to set
     */
    public void setIsSuspended(boolean suspend)
    {
        if (!isCanceled() && !isSuspended() && suspend)
        {
            this.lastUpdateTime = System.currentTimeMillis();
            this.duration = -2;
        }
    }


    /**
     * <p>Returns if the {@link Lease} has been issued indefinitely and thus never expires.</p>
     * 
     * @return if the {@link Lease} has been issued indefinitely and thus never expires.
     */
    public boolean isIndefinite()
    {
        return duration == -3;
    }


    /**
     * <p>Returns if the {@link Lease} is valid at the specified time.</p>
     * 
     * @param time The time in milliseconds (since the epoc)
     * 
     *  @return if the {@link Lease} is valid at the specified time.
     */
    public boolean isValidAt(long time)
    {
        return isIndefinite() || (!isCanceled() && !isSuspended() && lastUpdateTime + duration >= time);
    }


    /**
     * <p>Attempts to extend the {@link Lease} by a specified number of milliseconds from the current point in time.</p>
     * 
     * @param duration the number of milliseconds to extend the lease by (from the current point in time)
     * 
     * @return <code>true</code> if extending the {@link Lease} was permitted.
     */
    public boolean extend(long duration)
    {
        if (isIndefinite())
        {
            this.lastUpdateTime = System.currentTimeMillis();
            return true;

        }
        else if (isCanceled())
        {
            return false;

        }
        else if (duration >= 0)
        {
            this.duration = duration;
            this.lastUpdateTime = System.currentTimeMillis();
            return true;

        }
        else
        {
            if (logger.isLoggable(Level.FINEST))
                logger.finest(String.format("Attempted to extend %s by a negative duration %d. Ignoring request", this,
                    duration));

            return false;
        }
    }


    /**
     * <p>Attempts to extend the {@link Lease} by the currently specified duration from the current point in time.</p>
     * 
     * @return <code>true</code> if extending the {@link Lease} was permitted.
     */
    public boolean extend()
    {
        return this.extend(getDuration());
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.acquisitionTime = ExternalizableHelper.readLong(in);
        this.lastUpdateTime = ExternalizableHelper.readLong(in);
        this.duration = ExternalizableHelper.readLong(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeLong(out, acquisitionTime);
        ExternalizableHelper.writeLong(out, lastUpdateTime);
        ExternalizableHelper.writeLong(out, duration);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.acquisitionTime = reader.readLong(0);
        this.lastUpdateTime = reader.readLong(1);
        this.duration = reader.readLong(2);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeLong(0, acquisitionTime);
        writer.writeLong(1, lastUpdateTime);
        writer.writeLong(2, duration);
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
        if (!(obj instanceof Lease))
            return false;
        Lease other = (Lease) obj;
        if (acquisitionTime != other.acquisitionTime)
            return false;
        if (duration != other.duration)
            return false;
        if (lastUpdateTime != other.lastUpdateTime)
            return false;
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        if (isIndefinite())
        {
            return String.format("Lease{acquistionTime=%s, lastUpdateTime=%s, duration=INDEFINITE}", new Date(
                acquisitionTime), new Date(lastUpdateTime));

        }
        else if (isSuspended())
        {
            return String.format("Lease{acquistionTime=%s, lastUpdateTime=%s, duration=SUSPENDED}", new Date(
                acquisitionTime), new Date(lastUpdateTime));

        }
        else if (isCanceled())
        {
            return String.format("Lease{acquistionTime=%s, lastUpdateTime=%s, duration=CANCELED}", new Date(
                acquisitionTime), new Date(lastUpdateTime));

        }
        else
        {
            return String.format("Lease{acquistionTime=%s, lastUpdateTime=%s, duration=%d}", new Date(acquisitionTime),
                new Date(lastUpdateTime), duration);
        }
    }
}
