package com.db.itrac.coherence.ext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.PofValueParser;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;

public abstract class AbstractBinaryProcessor
        extends AbstractProcessor
        implements PortableObject
    {
    // ----- abstract methods -----------------------------------------------

    /**
     * Process a single binary entry.
     *
     * @param entry  entry to process
     *
     * @return processing result
     */
    protected abstract Object process(BinaryEntry entry);


    // ----- AbstractProcessor implementation -------------------------------

    /**
     * {@inheritDoc}
     */
    public Object process(InvocableMap.Entry entry)
        {
        return process((BinaryEntry) entry);
        }


    // ---- helper methods --------------------------------------------------

    /**
     * Return PofContext to use.
     *
     * @return PofContext to use
     */
    public PofContext getPofContext()
        {
        return m_pofContext;
        }

    /**
     * Return binary value with the specified name.
     *
     * @param name  name of the binary value
     *
     * @return binary value with the specified name
     */
    protected Binary getBinaryValue(String name)
        {
        return (Binary) m_binValues.get(name);
        }

    /**
     * Associate binary value with the specified name.
     *
     * @param name   name to associate value with
     * @param value  binary value
     */
    protected void setBinaryValue(String name, Binary value)
        {
        m_binValues.put(name, value);
        }

    /**
     * Return PofValue for the specified binary value.
     *
     * @param binValue  binary value to parse
     *
     * @return parsed PofValue
     */
    protected PofValue getPofValue(Binary binValue)
        {
        return PofValueParser.parse(binValue, m_pofContext);
        }

    /**
     * Return PofValue for the specified binary value's name.
     *
     * @param name  name of the binary value to parse
     *
     * @return parsed PofValue
     *
     * @see #setBinaryValue(String, Binary)
     */
    protected PofValue getPofValue(String name)
        {
        return getPofValue(getBinaryValue(name));
        }

    /**
     * Return deserialized object for the specified binary value.
     *
     * @param binValue  binary value to deserialize
     *
     * @return deserialized object
     */
    protected Object fromBinary(Binary binValue)
        {
        return ExternalizableHelper.fromBinary(binValue, m_pofContext);
        }

    /**
     * Return deserialized object for the specified binary value's name.
     *
     * @param name  name of the binary value to parse
     *
     * @return deserialized object
     *
     * @see #setBinaryValue(String, Binary)
     */
    protected Object fromBinary(String name)
        {
        return fromBinary(getBinaryValue(name));
        }

    /**
     * Serialize specified object into binary value.
     *
     * @param o  object to serialize
     *
     * @return serialized binary value
     */
    protected Binary toBinary(Object o)
        {
        return ExternalizableHelper.toBinary(o, m_pofContext);
        }


    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader  POF reader to use
     *
     * @throws IOException  if an error occurs during deserialization
     */
    public void readExternal(PofReader reader)
            throws IOException
        {
        m_pofContext = reader.getPofContext();
        }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer  POF writer to use
     *
     * @throws IOException  if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer)
            throws IOException
        {
        m_pofContext = writer.getPofContext();
        }

   
    // ---- Object methods --------------------------------------------------

    /**
     * Test objects for equality.
     *
     * @param o  object to compare this object with
     *
     * @return <tt>true</tt> if the specified object is equal to this object
     *         <tt>false</tt> otherwise
     */
    @Override
    public boolean equals(Object o)
        {
        if (this == o)
            {
            return true;
            }
        if (o == null || !(o instanceof AbstractBinaryProcessor))
            {
            return false;
            }

        AbstractBinaryProcessor processor = (AbstractBinaryProcessor) o;

        return (m_binValues == null
                 ? processor.m_binValues == null
                 : m_binValues.equals(processor.m_binValues));
        }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode()
        {
        return m_binValues != null ? m_binValues.hashCode() : 0;
        }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString()
        {
        return "AbstractBinaryProcessor{" +
               "pofContext=" + m_pofContext +
               "binValues=" + m_binValues +
               '}';
        }

    // ---- data members ----------------------------------------------------

    /**
     * PofContext to use.
     */
    private transient PofContext m_pofContext;

    /**
     * Storage for named binary values.
     */
    private transient Map m_binValues = new HashMap();
    }

