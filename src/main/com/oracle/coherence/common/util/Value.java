/*
 * File: Value.java
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.run.xml.XmlValue;
import com.tangosol.util.ExternalizableHelper;

/**
 * <p>A {@link Value} is an immutable object that represents a value whose type will only become known at runtime.</p>
 * 
 * <p>Much like a <a href="http://en.wikipedia.org/wiki/Variant_type">Variant</a> (wikipedia) it allows for runtime
 * coercion into other types, as and when required.</p>
 * 
 * <p>NOTE: {@link Value}s are very different from Generic types in that a). they retain their type information at 
 * runtime and b). may be cast/coerced into specific concrete types at runtime. {@link Value}s are thus often to used 
 * to represent values of potentially unknown types at runtime, simply because {@link Value}s provide an 
 * easy-to-use mechanism for coersion.</p>
 *
 * @see Coercer
 *
 * @author Brian Oliver
 */
@SuppressWarnings("serial")
public class Value implements ExternalizableLite, PortableObject
{

    /**
     * <p>The value of the {@link Value}.</p>
     */
    private Object value;

    /**
     * <p>The table of {@link Coercer} implementations for coercing {@link Value} values.</p>
     */
    private static HashMap<Class<?>, Coercer<?>> COERCERS;


    /**
     * <p>Standard Constructor for a <code>null</code> {@link Value}.</p>
     */
    public Value()
    {
        this.value = null;
    }


    /**
     * <p>Standard Constructor for a {@link Boolean}-based {@link Value}.</p>
     */
    public Value(boolean value)
    {
        this.value = value;
    }


    /**
     * <p>Standard Constructor for an {@link Object} based on another {@link Value}.</p>
     */
    public Value(Object value)
    {
        this.value = value;
    }


    /**
     * <p>Standard Constructor for a {@link String}-based {@link Value}.</p>
     */
    public Value(String value)
    {
        this.value = value.trim();
    }


    /**
     * <p>Standard Constructor for a {@link Value} based on another {@link Value}.</p>
     */
    public Value(Value value)
    {
        this.value = value.value;
    }


    /**
     * <p>Standard Constructor for an {@link XmlValue}-based {@link Value}.</p>
     */
    public Value(XmlValue xmlValue)
    {
        this.value = xmlValue;
    }


    /**
     * <p>Return if the {@link Value} represents a <code>null</code> value.</p>
     * 
     * @return <code>true</code> if the value of the {@link Value} is <code>null</code>, otherwise <code>false</code>
     */
    public boolean isNull()
    {
        return value == null;
    }


    /**
     * <p>Return the underlying {@link Object} representation of the {@link Value} value.<p>
     * 
     * @return The {@link Object} representation of the {@link Value}. (may be <code>null</code>).
     */
    public Object getObject()
    {
        return value;
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as a boolean.</p>
     * 
     * @return The value as a boolean.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public boolean getBoolean() throws ClassCastException
    {
        return getValue(Boolean.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as a byte.</p>
     * 
     * @return The value as a byte.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public byte getByte() throws ClassCastException
    {
        return getValue(Byte.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as a double.</p>
     * 
     * @return The value as a double.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public double getDouble() throws ClassCastException
    {
        return getValue(Double.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as a float.</p>
     * 
     * @return The value as a float.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public float getFloat() throws ClassCastException
    {
        return getValue(Float.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as an int.</p>
     * 
     * @return The value as a int.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public int getInt() throws ClassCastException
    {
        return getValue(Integer.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as a long.</p>
     * 
     * @return The value as a long.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public long getLong() throws ClassCastException
    {
        return getValue(Long.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} as a short.</p>
     * 
     * @return The value as a short.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public short getShort() throws ClassCastException
    {
        return getValue(Short.class);
    }


    /**
     * <p>Attempts to coerce and return the value of the {@link Value} into a String.</p>
     * 
     * @return The value as a String.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the require type.
     */
    public String getString() throws ClassCastException
    {
        return getValue(String.class);
    }


    /**
     * <p>Determines if the {@link Value} supports coercion with a {@link Coercer} to the specified type.</p>
     * 
     * <p><strong>NOTE:</strong> This does not test whether the {@link Value} can be coerced without an exception. It
     * simply determines if a {@link Coercer} for the specified type is known to the {@link Value}.</p>
     *  
     * @param clazz The type to which the {@link Value} should be coerced.
     * @return <code>true</code if type is a known {@link Value} {@link Coercer}.  <code>false</code> otherwise.</p> 
     */
    public boolean hasCoercerFor(Class<?> clazz)
    {
        return clazz.isEnum() || clazz.isPrimitive() || COERCERS.containsKey(clazz);
    }


    /**
     * <p>Attempts to return the value of the {@link Value} coerced to a specified type.
     *  
     * @param <T> The expected type of the value.
     * @param clazz The expected type of the value (the value to coerce to)
     * 
     * @return The {@link Value} coerced in the required type.
     * 
     * @throws ClassCastException If the value of the {@link Value} can't be cast to the specified type.
     */
    @SuppressWarnings({ "unchecked" })
    public <T> T getValue(Class<T> clazz) throws ClassCastException
    {
        if (isNull())
        {
            return null;
        }
        else if (clazz.equals(value.getClass()))
        {
            return (T) value;
        }
        else if (clazz.isAssignableFrom(value.getClass()))
        {
            return (T) value;
        }
        else if (clazz.isEnum())
        {
            try
            {
                return (T) Enum.valueOf((Class<Enum>) clazz, value instanceof XmlValue
                        ? ((XmlValue) value).getString() : value.toString());
            }
            catch (Exception exception)
            {
                //the enum is unknown/unsupported
                throw new ClassCastException(String.format("The specified Enum value '%s' is unknown.", value));
            }
        }
        else
        {
            if (COERCERS.containsKey(clazz))
            {
                Coercer<T> coercer = (Coercer<T>) COERCERS.get(clazz);
                return coercer.coerce(this);
            }
            else
            {
                throw new ClassCastException(String.format("Can't cast %s into a %s.", value, clazz.toString()));
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(DataInput in) throws IOException
    {
        this.value = ExternalizableHelper.readObject(in);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(DataOutput out) throws IOException
    {
        ExternalizableHelper.writeObject(out, value);
    }


    /**
     * {@inheritDoc}
     */
    public void readExternal(PofReader reader) throws IOException
    {
        this.value = reader.readObject(1);
    }


    /**
     * {@inheritDoc}
     */
    public void writeExternal(PofWriter writer) throws IOException
    {
        writer.writeObject(1, value);
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("Variant{%s}", value);
    }


    /**
     * <p>A {@link Coercer} is responsible for performing type coercion on a {@link Value} to obtain a required type
     * of value.</p>
     */
    public static interface Coercer<T>
    {

        /**
         * <p>Attempts to coerce the specified {@link Value} into the required type.</p>
         * 
         * @param value The {@link Value} to coerce. (will never be <code>null</code>)
         * 
         * @return The value of the {@link Value} coerced into the required type.
         * 
         * @throws ClassCastException should the coercion fail.
         */
        public T coerce(Value value) throws ClassCastException;
    }


    /**
     * <p>A {@link BigDecimalCoercer} is a {@link BigDecimal}-based implementation of a {@link Coercer}.</p>
     */
    private static class BigDecimalCoercer implements Coercer<BigDecimal>
    {

        /**
         * {@inheritDoc}
         */
        public BigDecimal coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return ((XmlValue) value.value).getDecimal();
            }
            else
            {
                return new BigDecimal(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link BooleanCoercer} is a {@link Boolean}-based implementation of a {@link Coercer}.</p>
     */
    private static class BooleanCoercer implements Coercer<Boolean>
    {

        /**
         * {@inheritDoc}
         */
        public Boolean coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return ((XmlValue) value.value).getBoolean();
            }
            else
            {
                return Boolean.parseBoolean(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link ByteCoercer} is a {@link Byte}-based implementation of a {@link Coercer}.</p>
     */
    private static class ByteCoercer implements Coercer<Byte>
    {

        /**
         * {@inheritDoc}
         */
        public Byte coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return Byte.parseByte(((XmlValue) value.value).getString());
            }
            else
            {
                return Byte.parseByte(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link DoubleCoercer} is a {@link Double}-based implementation of a {@link Coercer}.</p>
     */
    private static class DoubleCoercer implements Coercer<Double>
    {

        /**
         * {@inheritDoc}
         */
        public Double coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return ((XmlValue) value.value).getDouble();
            }
            else
            {
                return Double.parseDouble(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link FloatCoercer} is a {@link Double}-based implementation of a {@link Coercer}.</p>
     */
    private static class FloatCoercer implements Coercer<Float>
    {

        /**
         * {@inheritDoc}
         */
        public Float coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return (float) ((XmlValue) value.value).getDouble();
            }
            else
            {
                return Float.parseFloat(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link IntegerCoercer} is a {@link Integer}-based implementation of a {@link Coercer}.</p>
     */
    private static class IntegerCoercer implements Coercer<Integer>
    {

        /**
         * {@inheritDoc}
         */
        public Integer coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return ((XmlValue) value.value).getInt();
            }
            else
            {
                return Integer.parseInt(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link LongCoercer} is a {@link Long}-based implementation of a {@link Coercer}.</p>
     */
    private static class LongCoercer implements Coercer<Long>
    {

        /**
         * {@inheritDoc}
         */
        public Long coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return ((XmlValue) value.value).getLong();
            }
            else
            {
                return Long.parseLong(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link ShortCoercer} is a {@link Short}-based implementation of a {@link Coercer}.</p>
     */
    private static class ShortCoercer implements Coercer<Short>
    {

        /**
         * {@inheritDoc}
         */
        public Short coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return (short) ((XmlValue) value.value).getInt();
            }
            else
            {
                return Short.parseShort(value.value.toString());
            }
        }
    }


    /**
     * <p>A {@link StringCoercer} is a {@link String}-based implementation of a {@link Coercer}.</p>
     */
    private static class StringCoercer implements Coercer<String>
    {

        /**
         * {@inheritDoc}
         */
        public String coerce(Value value) throws ClassCastException
        {
            if (value.value instanceof XmlValue)
            {
                return ((XmlValue) value.value).getString();
            }
            else
            {
                return value.value.toString();
            }
        }
    }

    /**
     * <p>Static initialization to build map of known {@link Coercer}s.</p>
     */
    static
    {
        //initialize the known coercers based on their type
        COERCERS = new HashMap<Class<?>, Coercer<?>>();
        COERCERS.put(BigDecimal.class, new BigDecimalCoercer());
        COERCERS.put(Boolean.class, new BooleanCoercer());
        COERCERS.put(Boolean.TYPE, new BooleanCoercer());
        COERCERS.put(Byte.class, new ByteCoercer());
        COERCERS.put(Byte.TYPE, new ByteCoercer());
        COERCERS.put(Double.class, new DoubleCoercer());
        COERCERS.put(Double.TYPE, new DoubleCoercer());
        COERCERS.put(Float.class, new FloatCoercer());
        COERCERS.put(Float.TYPE, new FloatCoercer());
        COERCERS.put(Integer.class, new IntegerCoercer());
        COERCERS.put(Integer.TYPE, new IntegerCoercer());
        COERCERS.put(Long.class, new LongCoercer());
        COERCERS.put(Long.TYPE, new LongCoercer());
        COERCERS.put(Short.class, new ShortCoercer());
        COERCERS.put(Short.TYPE, new ShortCoercer());
        COERCERS.put(String.class, new StringCoercer());
    }
}