/*
 * File: Tristate.java
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
package com.oracle.coherence.common.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * <p>A {@link Tristate} is an enumeration that represents three values, TRUE, FALSE, UNDEFINED.</p>
 *  
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Tristate implements ExternalizableLite, PortableObject
{
    /**
     * <p>The undefined value for a {@link Tristate}.</p>
     */
    public static final Tristate UNDEFINED = new Tristate((byte) -1);

    /**
     * <p>The false value for a {@link Tristate}.</p>
     */
    public static final Tristate FALSE = new Tristate((byte) 0);

    /**
     * <p>The true value for a {@link Tristate}.</p>
     */
    public static final Tristate TRUE = new Tristate((byte) 1);

    /**
     * <p>The value of a {@link Tristate}.</p>
     */
    private byte value;


    /**
     * <p>Required for {@link ExternalizableLite} and {@link PortableObject}.</p>
     */
    public Tristate()
    {
    }


    /**
     * <p>Private Constructor: You should use the statics when a specific value is required.</p>
     * 
     * @param value The value of the {@link Tristate}
     */
    private Tristate(byte value)
    {
        this.value = value;
    }


    /**
     * <p>Standard Constructor to create a {@link Tristate} from a {@link Boolean}.</p>
     * 
     * @param value The boolean value for initialization.
     */
    public Tristate(boolean value)
    {
        this.value = value ? (byte) 1 : (byte) 0;
    }


    /**
     * <p>Returns if the {@link Tristate} is TRUE.</p>
     * 
     * @return <code>true</code> if the {@link Tristate} value is TRUE.
     */
    public boolean isTrue()
    {
        return value == (byte) 1;
    }


    /**
     * <p>Returns if the {@link Tristate} is FALSE.</p>
     * 
     * @return <code>true</code> if the {@link Tristate} value is FALSE.
     */
    public boolean isFalse()
    {
        return value == (byte) 0;
    }


    /**
     * <p>Returns if the {@link Tristate} is UNDEFINED.</p>
     * 
     * @return <code>true</code> if the {@link Tristate} value is UNDEFINED.
     */
    public boolean isUndefined()
    {
        return value == (byte) -1;
    }


    /**
     * <p>Returns the negation (not) of the {@link Tristate}.</p>
     * 
     * <code>
     *   not(True)      = False
     *   not(False)     = True
     *   not(Undefined) = Undefined
     * </code>
     * 
     * @return The negation (not) of a {@link Tristate}.
     */
    public Tristate not()
    {
        if (isUndefined())
        {
            return this;
        }
        else
        {
            return isTrue() ? Tristate.FALSE : Tristate.TRUE;
        }
    }


    /**
     * <p>Returns the disjunction (or) of this {@link Tristate} and another.</p>
     * 
     * <code>
     *   True or True           = True
     *   True or False          = True
     *   True or Undefined      = True
     *   False or True          = True 
     *   False or False         = False
     *   False or Undefined     = False
     *   Undefined or True      = True
     *   Undefined or False     = False
     *   Undefined or Undefined = Undefined
     * </code>
     * 
     * @param other The other {@link Tristate} with which to perform the disjunction 
     * 
     * @return The disjunction (or) of this and another {@link Tristate}.
     */
    public Tristate or(Tristate other)
    {
        if (isTrue())
        {
            return this;
        }
        else if (isFalse())
        {
            return other.isTrue() ? other : Tristate.FALSE;
        }
        else
        {
            return other;
        }
    }


    /**
     * <p>Returns the conjunction (and) of this {@link Tristate} and another.</p>
     * 
     * <code>
     *   True and True           = True
     *   True and False          = False
     *   True and Undefined      = Undefined
     *   False and True          = False 
     *   False and False         = False
     *   False and Undefined     = Undefined
     *   Undefined and True      = Undefined
     *   Undefined and False     = Undefined
     *   Undefined and Undefined = Undefined
     * </code>
     * 
     * @param other The other {@link Tristate} with which to perform the conjunction 
     * 
     * @return The conjunction (and) of this and another {@link Tristate}.
     */
    public Tristate and(Tristate other)
    {
        if (isTrue())
        {
            return other;
        }
        else if (isFalse())
        {
            return other.isUndefined() ? other : Tristate.FALSE;
        }
        else
        {
            return Tristate.UNDEFINED;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.value = in.readByte();
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        out.writeByte(value);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.value = reader.readByte(1);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeByte(1, value);
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return value;
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
        if (!(obj instanceof Tristate))
            return false;
        Tristate other = (Tristate) obj;
        if (value != other.value)
            return false;
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("Tristate{%s}", value == 0 ? "FALSE" : (value == 1 ? "TRUE" : "UNDEFINED"));
    }
}
