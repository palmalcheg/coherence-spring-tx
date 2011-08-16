/*
 * File: PropertiesScheme.java
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
package com.oracle.coherence.system.application;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A {@link PropertiesScheme} defines a set of property definitions that when realized may be used as
 * a traditional {@link Map} of name value pair properties.
 * <p>
 * Unlike traditional {@link Map}-based implementations of properties, a {@link PropertiesScheme} provides
 * the ability to specify an {@link Iterator} for named properties that will in turn be used to acquire 
 * actual property values when the said {@link PropertiesScheme} is realized.
 *
 * @author Brian Oliver
 */
public class PropertiesScheme
{

    /**
     * The properties defined by the {@link PropertiesScheme}.
     */
    private LinkedHashMap<String, Object> m_Properties;


    /**
     * Standard Constructor (produces an empty {@link PropertiesScheme}).
     */
    public PropertiesScheme()
    {
        m_Properties = new LinkedHashMap<String, Object>();
    }


    /**
     * Standard Constructor that is based on a copy of an existing set of properties.
     * 
     * @param properties The {@link Map} of properties to use as the basis for the {@link PropertiesScheme}
     */
    public PropertiesScheme(Map<String, String> properties)
    {
        this();
        for (String name : properties.keySet())
        {
            m_Properties.put(name, properties.get(name));
        }
    }


    /**
     * Standard Constructor that is based on a copy of an existing set of properties.
     * 
     * @param properties The {@link Map} of properties to use as the basis for the {@link PropertiesScheme}
     */
    public PropertiesScheme(Properties properties)
    {
        this();
        for (Object key : properties.keySet())
        {
            m_Properties.put((String) key, properties.get(key));
        }
    }


    /**
     * Standard Constructor that is based on a copy of another {@link PropertiesScheme}.
     * 
     * @param propertyScheme The {@link PropertiesScheme} on which to base the new {@link PropertiesScheme}
     */
    public PropertiesScheme(PropertiesScheme propertyScheme)
    {
        this();
        for (String name : propertyScheme.getPropertyNames())
        {
            m_Properties.put(name, propertyScheme.getProperty(name));
        }
    }


    /**
     * Sets the specified named property to use an {@link Iterator} to provide successive property values when
     * the {@link PropertiesScheme} is realized.
     * 
     * @param name     The name of the property.
     * 
     * @param iterator An iterator that will provide successive property values for the property 
     *                 when the {@link PropertiesScheme} is realized.
     */
    public void setProperty(String name,
                            Iterator<?> iterator)
    {
        m_Properties.put(name, iterator);
    }


    /**
     * Sets the specified named property to have the specified value.
     * 
     * @param name      The name of the property
     * @param value     The value of the property
     */
    public void setProperty(String name,
                            Object value)
    {
        m_Properties.put(name, value);
    }


    /**
     * Adds and/or overrides the properties defined in the {@link PropertiesScheme} with those from the specified
     * {@link PropertiesScheme}.
     *  
     * @param propertyScheme The {@link PropertiesScheme} containing the properties to add to this {@link PropertiesScheme}.
     */
    public void setProperties(PropertiesScheme propertyScheme)
    {
        m_Properties.putAll(propertyScheme.m_Properties);
    }


    /**
     * Sets the specified named property to use an {@link Iterator} to provide successive property values when
     * the {@link PropertiesScheme} is realized.
     * 
     * @param name     The name of the property.
     * 
     * @param iterator An iterator that will provide successive property values for the property 
     *                 when the {@link PropertiesScheme} is realized.
     *                 
     * @return The {@link PropertiesScheme} to which the property was added so that further chained method calls, like
     *         to other <code>withProperty(...)</code> methods on this class may be used. 
     */
    public PropertiesScheme withProperty(String name,
                                       Iterator<?> iterator)
    {
        setProperty(name, iterator);
        return this;
    }


    /**
     * Sets the specified named property to have the specified value.
     * 
     * @param name      The name of the property
     * 
     * @param value     The value of the property
     *                 
     * @return The {@link PropertiesScheme} to which the property was added so that further chained method calls, like
     *         to other <code>withProperty(...)</code> methods on this class may be used. 
     */
    public PropertiesScheme withProperty(String name,
                                       Object value)
    {
        setProperty(name, value);
        return this;
    }


    /**
     * Adds and/or overrides the properties defined in the {@link PropertiesScheme} with those from the specified
     * {@link PropertiesScheme}.
     *  
     * @param propertyScheme The {@link PropertiesScheme} containing the properties to add to this {@link PropertiesScheme}.
     *                 
     * @return The {@link PropertiesScheme} to which the property was added so that further chained method calls, like
     *         to other <code>withProperty(...)</code> methods on this class may be used. 
     */
    public PropertiesScheme withProperties(PropertiesScheme propertyScheme)
    {
        setProperties(propertyScheme);
        return this;
    }


    /**
     * Returns if the specified named property is defined by the {@link PropertiesScheme}.
     * 
     * @param name  The name of the property
     * 
     * @return <code>true</code> if the property is defined by the {@link PropertiesScheme}, <code>false</code> otherwise.
     */
    public boolean containsProperty(String name)
    {
        return m_Properties.containsKey(name);
    }


    public Object getProperty(String name)
    {
        if (m_Properties.containsKey(name))
        {
            return m_Properties.get(name);
        }
        else
        {
            return null;
        }
    }


    /**
     * Removes the specified named property from the {@link PropertiesScheme}.  If the specified property is not
     * contained by the {@link PropertiesScheme}, nothing happens.
     * 
     * @param name The name of the property to remove.
     */
    public void removeProperty(String name)
    {
        m_Properties.remove(name);
    }


    /**
     * Clears all of the currently defined properties from the {@link PropertiesScheme}.
     */
    public void clear()
    {
        m_Properties.clear();
    }


    /**
     * Returns an {@link Iterable} over the property names defined by the {@link PropertiesScheme}.
     * 
     * @return {@link Iterable}
     */
    public Iterable<String> getPropertyNames()
    {
        return m_Properties.keySet();
    }


    /**
     * Creates a new {@link Map} containing name, value pairs defined by the {@link PropertiesScheme}. If a property
     * with in the {@link PropertiesScheme} is defined as an {@link Iterator}, a value from the said {@link Iterator}
     * is used.
     * 
     * @param defaultPropertyScheme (optional may be <code>null</code>) This {@link PropertiesScheme} may be 
     *                              specified to provide an inherited/default/base set of initial properties for the 
     *                              resulting {@link Map}.  These initial properties are then overriden by those 
     *                              specified in the current {@link PropertiesScheme} prior to the {@link Map} 
     *                              being returned. 
     * 
     * @return A new {@link Map} of the properties defined by the {@link PropertiesScheme}
     */
    public Map<String, String> realize(PropertiesScheme defaultPropertyScheme)
    {
        HashMap<String, String> properties = new HashMap<String, String>();

        //add all of the default properties first
        if (defaultPropertyScheme != null)
        {
            properties.putAll(defaultPropertyScheme.realize(null));
        }

        for (String name : getPropertyNames())
        {
            Object value = getProperty(name);

            if (value != null)
            {
                if (value instanceof Iterator<?>)
                {
                    Iterator<?> iterator = (Iterator<?>) value;
                    if (iterator.hasNext())
                    {
                        properties.put(name, iterator.next().toString());
                    }
                    else
                    {
                        throw new IndexOutOfBoundsException(String.format(
                            "No more values available for the property [%s]", name));
                    }
                }
                else
                {
                    properties.put(name, value.toString());
                }
            }
        }

        return properties;
    }


    /**
     * Constructs a {@link PropertiesScheme} using the properties defined in the specified Java properties file.
     * 
     * @param fileName The name of the file (including path if required) from which to load the properties
     * 
     * @return A {@link PropertiesScheme}
     * 
     * @throws IOException Should a problem occur whil loading the properties
     */
    public static PropertiesScheme fromPropertiesFile(String fileName) throws IOException
    {
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource(fileName);
        properties.load(url.openStream());

        return new PropertiesScheme(properties);
    }


    /**
     * Constructs a {@link PropertiesScheme} using the environment variables of the currently executing process.
     * 
     * @return A {@link PropertiesScheme}
     */
    public static PropertiesScheme fromCurrentEnvironmentVariables()
    {
        return new PropertiesScheme(System.getenv());
    }


    /**
     * Constructs a {@link PropertiesScheme} using the current system properties the currently executing process.
     * 
     * @return A {@link PropertiesScheme}
     */
    public static PropertiesScheme fromCurrentSystemProperties()
    {
        return new PropertiesScheme(System.getProperties());
    }
}
