/*
 * File: CreateRemoteObjectProcessor.java
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
package com.oracle.coherence.common.processors;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.util.ReflectionHelper;
import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.Base;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * The {@link CreateRemoteObjectProcessor} will create an object of the supplied
 * class, matching it to a constructor that matches the supplied parameters.
 * 
 * If the 
 * 
 * @author Christer Fahlgren 2009.12.20
 */
@SuppressWarnings("serial")
public class CreateRemoteObjectProcessor extends AbstractProcessor implements ExternalizableLite, PortableObject
{
    /**
     * The logger to use.
     */
    private static final Logger logger = Logger.getLogger(CreateRemoteObjectProcessor.class.getName());

    /**
     * The class name of the class we will instantiate.
     */
    private String className;

    /**
     * The parameters to pass to the constructor.
     */
    protected Object parameters[];


    /**
     * Default constructor.
     */
    public CreateRemoteObjectProcessor()
    {
    }


    /**
     * Standard constructor.
     * 
     * @param classname  the name of the class to instantiate.
     * @param parameters array of constructor parameters
     */
    public CreateRemoteObjectProcessor(String classname,
                                       Object... parameters)
    {
        className       = classname;
        this.parameters = parameters;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object process(Entry entry)
    {
        if (entry.isPresent())
        {
            return new IllegalStateException("An object with the key " + entry.getKey() + " already exists");
        }
        else
        {

            Class clazz;
            try
            {
                clazz = Class.forName(className);
            }
            catch (ClassNotFoundException e)
            {
                logger.log(Level.SEVERE, "Exception in process method ", e);
                throw Base.ensureRuntimeException(e);
            }
            try
            {
                Class[] parameterTypes = null;
                if (parameters != null)
                {
                    parameterTypes = new Class[parameters.length];
                    for (int i = 0; i < parameters.length; i++)
                    {
                        parameterTypes[i] = parameters[i].getClass();
                    }
                }
                Constructor con = ReflectionHelper.getCompatibleConstructor(clazz, parameterTypes);

                Object inst = con.newInstance(parameters);
                entry.setValue(inst);
                return null;
            }
            catch (InstantiationException e)
            {
                return e;
            }
            catch (IllegalAccessException e)
            {
                return e;
            }
            catch (Exception e)
            {
                return e;
            }
        }
    }


    /**
    * {@inheritDoc}
    */
    public void readExternal(final DataInput in) throws IOException
    {
        this.className = ExternalizableHelper.readUTF(in);
        int cParams = ExternalizableHelper.readInt(in);
        Object[] aoParam = cParams == 0 ? null : new Object[cParams];

        for (int i = 0; i < cParams; i++)
        {
            aoParam[i] = ExternalizableHelper.readObject(in);
        }
        parameters = aoParam;

    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(final DataOutput out) throws IOException
    {
        ExternalizableHelper.writeUTF(out, className);
        Object[] aoParam = parameters;
        int cParams = aoParam == null ? 0 : aoParam.length;

        ExternalizableHelper.writeInt(out, cParams);
        for (int i = 0; i < cParams; i++)
        {
            ExternalizableHelper.writeObject(out, aoParam[i]);
        }

    }


    /**
    * {@inheritDoc}
    */
    public void readExternal(final PofReader oReader) throws IOException
    {
        this.className = oReader.readString(0);
        this.parameters = oReader.readObjectArray(1, null);
    }


    /**
    * {@inheritDoc}
    */
    public void writeExternal(final PofWriter oWriter) throws IOException
    {
        oWriter.writeString(0, className);
        oWriter.writeObjectArray(1, parameters);
    }
}
