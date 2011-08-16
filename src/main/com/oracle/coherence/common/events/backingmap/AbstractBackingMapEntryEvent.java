/*
 * File: AbstractBackingMapEntryEvent.java
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
package com.oracle.coherence.common.events.backingmap;

import java.util.Map;
import java.util.Map.Entry;

import com.tangosol.coherence.reporter.extractor.KeyExtractor;
import com.tangosol.io.Serializer;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.ValueUpdater;
import com.tangosol.util.extractor.EntryExtractor;

/**
 * <p>A base implementation for {@link BackingMapEntryEvent}s.</p>
 * 
 * @author Brian Oliver
 */
public abstract class AbstractBackingMapEntryEvent implements BackingMapEntryEvent
{

    /**
     * <p>The {@link BackingMapManagerContext} in which the {@link BackingMapEntryEvent} occurred.</p>
     */
    protected BackingMapManagerContext backingMapManagerContext;

    /**
     * <p>The name of the {@link com.tangosol.net.NamedCache} on which the {@link BackingMapEntryEvent} occurred.</p>
     */
    private String cacheName;

    /**
     * <p>The internal presentation of the {@link Entry} for the {@link BackingMapEntryEvent}.
     */
    private EventEntry entry;


    /**
     * <p>Standard Constructor (when using {@link Entry}s).</p>
     * 
     * @param backingMapManagerContext The BackingMapManagerContext associated with this event
     * @param cacheName                The name of the cache where this event was triggered
     * @param entry                    The {@link Entry} associated with this event
     */
    public AbstractBackingMapEntryEvent(BackingMapManagerContext backingMapManagerContext,
                                        String cacheName,
                                        Map.Entry<?, ?> entry)
    {
        this.backingMapManagerContext = backingMapManagerContext;
        this.cacheName = cacheName;

        if (entry instanceof EventEntry)
        {
            this.entry = (EventEntry) entry;
        }
        else if (entry instanceof BinaryEntry)
        {
            this.entry = new EventEntry((BinaryEntry) entry);
        }
        else
        {
            this.entry = new EventEntry(entry.getKey(), entry.getValue());
        }
    }


    /**
     * <p>Standard Constructor (when using key, value pairs).</p>
     * 
     * @param backingMapManagerContext The BackingMapManagerContext associated with this event
     * @param cacheName                The name of the cache where this event was triggered
     * @param key                      The key associated with this event
     * @param value                    The value associated with this event
     */
    public AbstractBackingMapEntryEvent(BackingMapManagerContext backingMapManagerContext,
                                        String cacheName,
                                        Object key,
                                        Object value)
    {
        this.backingMapManagerContext = backingMapManagerContext;
        this.cacheName = cacheName;

        this.entry = new EventEntry(key, value);
    }


    /**
     * {@inheritDoc}
     */
    public BackingMapManagerContext getContext()
    {
        return backingMapManagerContext;
    }


    /**
     * {@inheritDoc}
     */
    public String getCacheName()
    {
        return cacheName;
    }


    /**
     * {@inheritDoc}
     */
    public Entry<?, ?> getEntry()
    {
        return entry;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return String.format("%s{cacheName=%s, entry=%s}", getClass().getName(), getCacheName(), getEntry());
    }


    /**
     * 
     * <p>A {@link EventEntry} EventEntry is an immutable representation of an Entry for use by the events system.</p>
     *
     * @author Brian Oliver
     */
    @SuppressWarnings("unchecked")
    protected class EventEntry implements Map.Entry, BinaryEntry
    {

        /**
         * <p>The {@link Binary} version of the key.</p>
         */
        private Binary binaryKey;

        /**
         * <p>The {@link Binary} version of the key.</p>
         */
        private Binary binaryValue;

        /**
         * <p>The deserialized key.</p>
         */
        private Object deserializedKey;

        /**
         * <p>The deserialized value.</p>
         */
        private Object deserializedValue;


        /**
         * <p>Object based constructor.</p>
         * 
         * @param key   The key of this {@link EventEntry}
         * @param value The value of this {@link EventEntry}
         */
        public EventEntry(Object key,
                          Object value)
        {
            if (key instanceof Binary)
            {
                this.binaryKey = (Binary)key;
                this.deserializedKey = Void.TYPE;       //Void.Type indicates not yet deserialized
            }
            else
            {   
                this.binaryKey = Binary.NO_BINARY;      //Binary.NO_BINARY indicates not yet serialized
                this.deserializedKey = key;
            }

            if (value instanceof Binary)
            {
                this.binaryValue = (Binary)value;
                this.deserializedValue = Void.TYPE;     //Void.Type indicates not yet deserialized
            }
            else 
            {
                this.binaryValue = Binary.NO_BINARY;    //Binary.NO_BINARY indicates not yet serialized
                this.deserializedValue = value;
            }
        }


        /**
         * <p>{@link BinaryEntry} based constructor.</p>
         * 
         * @param binaryEntry The {@link BinaryEntry} to build this EventEntry out of
         */
        public EventEntry(BinaryEntry binaryEntry)
        {
            this.binaryKey = binaryEntry.getBinaryKey();
            this.binaryValue = binaryEntry.getBinaryValue();
            this.deserializedKey = Void.TYPE;
            this.deserializedValue = Void.TYPE;
        }


        /**
         * {@inheritDoc}
         */
        public Serializer getSerializer()
        {
            return getContext().getCacheService().getSerializer();
        }


        /**
         * {@inheritDoc}
         */
        public BackingMapManagerContext getContext()
        {
            return backingMapManagerContext;
        }


        /**
         * {@inheritDoc}
         */
        public Object getKey()
        {
            if (deserializedKey == Void.TYPE)
            {
                deserializedKey = getContext().getKeyFromInternalConverter().convert(binaryKey);
            }
            
            return deserializedKey;
        }


        /**
         * {@inheritDoc}
         */
        public Binary getBinaryKey()
        {
            if (binaryKey == Binary.NO_BINARY)
            {
                binaryKey = (Binary) getContext().getKeyToInternalConverter().convert(deserializedKey);
                
            }
            
            return binaryKey;
        }


        /**
         * {@inheritDoc}
         */
        public Object getValue()
        {
            if (deserializedValue == Void.TYPE)
            {
                deserializedValue = getContext().getValueFromInternalConverter().convert(binaryValue);
            }
            
            return deserializedValue;
        }


        /**
         * {@inheritDoc}
         */
        public Binary getBinaryValue()
        {
            if (binaryValue == Binary.NO_BINARY)
            {
                binaryValue = (Binary) getContext().getValueToInternalConverter().convert(deserializedValue);
                
            }
            
            return binaryValue;
        }


        /**
         * {@inheritDoc}
         */
        public Object extract(ValueExtractor valueExtractor)
        {
            if (valueExtractor == null)
            {
                return null;
            }
            else if (valueExtractor instanceof KeyExtractor)
            {
                return valueExtractor.extract(getKey());
            }
            else if (valueExtractor instanceof EntryExtractor)
            {
                return valueExtractor.extract(this);
            }
            else
            {
                return valueExtractor.extract(getValue());
            }
        }


        /**
         * {@inheritDoc}
         */
        public Object setValue(Object value)
        {
            throw new UnsupportedOperationException(
                "Can not call setValue(Object) an an EventEntry as they are immutable");
        }


        /**
         * {@inheritDoc}
         */
        public void updateBinaryValue(Binary binary)
        {
            throw new UnsupportedOperationException(
                "Can not call updateBinaryValue(Binary) an an EventEntry as they are immutable");
        }


        /**
         * {@inheritDoc}
         */
        public boolean isPresent()
        {
            //NOTE: always returns true as when the event was raises the entry was present
            return true;
        }


        /**
         * {@inheritDoc}
         */
        public void remove(boolean synthetic)
        {
            throw new UnsupportedOperationException(
                "Can not call remove(boolean) an an EventEntry as they are immutable");
        }


        /**
         * {@inheritDoc}
         */
        public void setValue(Object value,
                             boolean synthetic)
        {
            throw new UnsupportedOperationException(
                "Can not call setValue(Object, boolean) an an EventEntry as they are immutable");
        }


        /**
         * {@inheritDoc}
         */
        public void update(ValueUpdater valueUpdater,
                           Object object)
        {
            throw new UnsupportedOperationException(
                "Can not call update(ValueUpdater, Object) an an EventEntry as they are immutable");
        }


        /**
         * EventEntries are immutable so there will not be an original value.
         * 
         * @return null
         */
        public Binary getOriginalBinaryValue()
        {
            return null;
        }


        /**
         * EventEntries are immutable so there will not be an original value.
         * 
         * @return null
         */
        public Object getOriginalValue()
        {
            return null;
        }


        /**
         * We don't expose the backing map from here. Null is returned
         * 
         * @return null.
         */
        public Map getBackingMap()
        {
            return null;
        }


        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return String.format("%s{key=%s, value=%s}", getClass().getName(), getKey(), getValue());
        }
    }
}
