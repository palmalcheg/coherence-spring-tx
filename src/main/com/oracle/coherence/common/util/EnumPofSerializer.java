/*
 * File: EnumSerializer.java
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
package com.oracle.coherence.common.util;

import java.io.IOException;

import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

/**
* {@link PofSerializer} implementation that can be used to serialize all {@link Enum}
* values.
*
* @author as  2008.10.24
*/
public class EnumPofSerializer implements PofSerializer
{

    /**
     * {@inheritDoc}
     */
    public void serialize(PofWriter writer,
                          Object o) throws IOException
    {
        if (!o.getClass().isEnum())
        {
            throw new IllegalArgumentException("EnumPofSerializer can only be used to serialize enum types.");
        }

        writer.writeString(0, ((Enum<?>) o).name());
        writer.writeRemainder(null);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object deserialize(PofReader reader) throws IOException
    {
        PofContext ctx = reader.getPofContext();
        Class<?> clz = ctx.getClass(reader.getUserTypeId());

        if (!clz.isEnum())
        {
            throw new IllegalArgumentException("EnumPofSerializer can only be used to deserialize enum types.");
        }

        Enum<?> enumValue = (Enum<?>) Enum.valueOf((Class<Enum>) clz, reader.readString(0));
        reader.readRemainder();

        return enumValue;
    }
}
