/*
 * File: CoherenceNamespaceContentHandler.java
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
package com.oracle.coherence.environment.extensible.namespaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.util.Value;
import com.oracle.coherence.configuration.Configurator;
import com.oracle.coherence.configuration.caching.CacheMapping;
import com.oracle.coherence.configuration.caching.CacheMappingRegistry;
import com.oracle.coherence.configuration.parameters.MutableParameterScope;
import com.oracle.coherence.configuration.parameters.Parameter;
import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.configuration.parameters.SimpleParameterScope;
import com.oracle.coherence.configuration.parameters.SystemPropertyParameterScope;
import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.oracle.coherence.schemes.ClassScheme;
import com.oracle.coherence.schemes.SchemeRegistry;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlValue;
import com.tangosol.util.Base;
import com.tangosol.util.Resources;
import com.tangosol.util.UUID;

/**
 * <p>The {@link CoherenceNamespaceContentHandler} is responsible for capturing and creating Coherence
 * Cache Configurations when processing a Coherence Configuration file.</p>
 * 
 * <p>NOTE 1: This is the default namespace handler for Coherence Cache Configuration files.</p>
 * 
 * <p>NOTE 2: This implementation has been refactored from the AdvancedConfigurableCacheFactory from
 * version 1.5.x of coherence-common.</p>
 * 
 * @author Brian Oliver
 */
public class CoherenceNamespaceContentHandler extends AbstractNamespaceContentHandler
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static final Logger logger = Logger.getLogger(CoherenceNamespaceContentHandler.class.getName());

    /**
     * <p>The currently collected set of cache scheme mappings as {@link XmlElement}s.</p>
     */
    private LinkedHashMap<String, XmlElement> cacheSchemeMappings;

    /**
     * <p>The currently collected set of cache scheme definitions as {@link XmlElement}s.</p>
     */
    private LinkedHashMap<String, XmlElement> cachingSchemes;

    /**
     * <p>The currently collected set of "defaults" declared using the Coherence namespace.  The key is the name
     * of the element.</p>
     */
    private LinkedHashMap<String, XmlElement> defaults;

    /**
     * <p>The set of xml element names that Coherence defines (from the cache-config.dtd).</p>
     */
    private HashSet<String> definedElementNames;


    /**
     * <p>Standard Constructor.</p>
     */
    public CoherenceNamespaceContentHandler()
    {
        this.cacheSchemeMappings = new LinkedHashMap<String, XmlElement>();
        this.cachingSchemes = new LinkedHashMap<String, XmlElement>();
        this.defaults = new LinkedHashMap<String, XmlElement>();
        this.definedElementNames = new HashSet<String>();

        registerContentHandler("class-scheme", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //determine if there's a single element from a custom-namespace in the <class-scheme>
                //(we're allowing single elements from custom-namespaces in <class-schemes>!)
                if (xmlElement.getElementList().size() == 1
                        && !new QualifiedName((XmlElement) xmlElement.getElementList().get(0)).getPrefix().equals(
                            getPrefix()))
                {
                    //create a unique id for the ClassScheme we're going to create using the custom-namespace element 
                    String id = new UUID().toString();

                    //modify the <class-scheme> to indicate that when it's instantiated by Coherence we 
                    //should use the uniquely identified ClassScheme
                    xmlElement.addAttribute("use-scheme").setString(id);

                    //create a ClassScheme using the child (scheme) Element
                    XmlElement schemeElement = (XmlElement) xmlElement.getElementList().get(0);
                    try
                    {
                        //process the schemeElement and assume it's a ClassScheme
                        ClassScheme<?> scheme = (ClassScheme<?>) context.processElement(schemeElement);

                        //register the ClassScheme with the associated id 
                        context.getEnvironment().getResource(SchemeRegistry.class).registerScheme(id, scheme);

                        //return the ClassScheme that we've created
                        return scheme;
                    }
                    catch (ClassCastException classCastException)
                    {
                        throw new RuntimeException(
                            String
                                .format(
                                    "%s can't be used within a <class-scheme> the namespace handler did not return a ClassScheme.",
                                    schemeElement), classCastException);
                    }

                }
                else
                {
                    //this is a traditional use of the Coherence <class-scheme>
                    //so just process the child elements as we normally would
                    return null;
                }
            }
        });

        registerContentHandler("cache-mapping", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {

                String cacheName = Configurator.getMandatoryProperty("cache-name", String.class, context,
                    qualifiedName, xmlElement);

                String schemeName = Configurator.getMandatoryProperty("scheme-name", String.class, context,
                    qualifiedName, xmlElement);

                ParameterScope parameterScope = Configurator.getOptionalProperty("init-params", ParameterScope.class,
                    SystemPropertyParameterScope.INSTANCE, context, qualifiedName, xmlElement);

                CacheMapping cacheMapping = new CacheMapping(cacheName, schemeName, parameterScope);

                //register the CacheMapping with the Environment
                if (context.getEnvironment().getResource(CacheMappingRegistry.class).addCacheMapping(cacheMapping))
                {
                    //FUTURE: We're currently only using CacheMappings with in the ExtensibleEnvironment.
                    //        Unfortunately this means we need to keep track of the actual xml for cache-mappings
                    //        so that we can provide them to the DCCF later.
                    //
                    //        we can remove the following line when we're no longer dependent on the DCCF
                    cacheSchemeMappings.put(cacheName, xmlElement);
                }

                //process all of the child elements (this allows them to modify the dom if required)
                context.processElementsOf(xmlElement);

                return cacheMapping;
            }
        });

        registerContentHandler("caching-schemes", new ElementContentHandler()
        {

            @SuppressWarnings("unchecked")
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //process the child elements (this allows them to modify the dom if required)
                context.processElementsOf(xmlElement);

                //now remember the schemes defined in the caching-schemes element so we 
                //can provide them to Coherence for configuration
                for (XmlElement xmlSchemeElement : (List<XmlElement>) xmlElement.getElementList())
                {
                    String schemeName;
                    XmlElement schemeNameElement = xmlSchemeElement.getElement("scheme-name");

                    if (schemeNameElement == null)
                    {
                        schemeName = new UUID().toString();
                    }
                    else
                    {
                        schemeName = schemeNameElement.getString();
                    }

                    cachingSchemes.put(schemeName, xmlSchemeElement);
                }

                //this element handler doesn't produce a result
                return null;
            }
        });

        registerContentHandler("defaults", new ElementContentHandler()
        {

            @SuppressWarnings("unchecked")
            @Override
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //process the children of the defaults element adding their xml to the defaults maps so we
                //can later provide this to the DCCF
                for (Iterator<XmlElement> iter = (Iterator<XmlElement>) xmlElement.getElementList().iterator(); iter
                    .hasNext();)
                {
                    XmlElement childElement = iter.next();

                    //warn if we've seen this default declaration previously
                    if (defaults.containsKey(childElement.getName()))
                    {
                        logger.log(Level.WARNING,
                            "WARNING: Overriding existing <defaults> definition for {0} with {1}", new Object[] {
                                    childElement.getName(), childElement.toString() });
                    }

                    //have the context process the element
                    context.processElement(childElement);

                    //remember/override the previous defaults declaration
                    defaults.put(childElement.getName(), childElement);
                }

                return null;
            }
        });

        registerContentHandler("init-param", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //when there is no param-name defined, use a newyl generated UUID
                String name = Configurator.getOptionalProperty("param-name", String.class, new UUID().toString(),
                    context, qualifiedName, xmlElement);

                Value value = Configurator.getMandatoryProperty("param-value", Value.class, context, qualifiedName,
                    xmlElement);

                //FUTURE: (optional) ensure that the param-value may be coerced into the optionally specified param-type.
                return new Parameter(name, value);
            }
        });

        registerContentHandler("init-params", new ElementContentHandler()
        {

            @SuppressWarnings("unchecked")
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //construct a scope to hold the specified init-params
                MutableParameterScope parameterScope = new SimpleParameterScope();

                //resolve all of the Parameters
                for (Iterator<XmlElement> children = (Iterator<XmlElement>) xmlElement.getElementList().iterator(); children
                    .hasNext();)
                {
                    XmlElement childElement = children.next();

                    Object childResult = context.processElement(childElement);

                    if (childResult != null && childResult instanceof Parameter)
                    {
                        parameterScope.addParameter((Parameter) childResult);
                    }
                    else
                    {
                        //the childElement is not a valid parameter
                        return new ConfigurationException(String.format("Invalid parameter definition '%s' in '%s'",
                            childElement, xmlElement), "Please ensure the parameter is correctly defined");
                    }
                }

                return parameterScope;
            }
        });

        registerContentHandler("param-name", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                return xmlElement.getString().trim();
            }
        });

        registerContentHandler("param-value", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //IMPORTANT: Coherence doesn't yet support this type of value nesting.  It simply supports simple values
                //           but we're providing it
                //here to that we can specify structured values.
                if (xmlElement.getElementList().size() == 1)
                {
                    return new Value(context.processOnlyElementOf(xmlElement));
                }
                else
                {
                    return new Value(xmlElement);
                }
            }
        });

        registerContentHandler("param-type", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                String typeName = xmlElement.getString();

                if (typeName.equalsIgnoreCase("string"))
                {
                    return String.class;
                }
                else if (typeName.equalsIgnoreCase("boolean"))
                {
                    return Boolean.class;
                }
                else if (typeName.equalsIgnoreCase("int"))
                {
                    return Integer.class;
                }
                else if (typeName.equalsIgnoreCase("long"))
                {
                    return Long.class;
                }
                else if (typeName.equalsIgnoreCase("double"))
                {
                    return Double.class;
                }
                else if (typeName.equalsIgnoreCase("decimal"))
                {
                    return BigDecimal.class;
                }
                else if (typeName.equalsIgnoreCase("file"))
                {
                    return File.class;
                }
                else if (typeName.equalsIgnoreCase("date"))
                {
                    return Date.class;
                }
                else if (typeName.equalsIgnoreCase("time"))
                {
                    return Time.class;
                }
                else if (typeName.equalsIgnoreCase("datetime"))
                {
                    return Timestamp.class;
                }
                else if (typeName.equalsIgnoreCase("xml"))
                {
                    return XmlElement.class;
                }
                else if (typeName.equalsIgnoreCase("classloader") || typeName.equalsIgnoreCase("java.lang.ClassLoader"))
                {
                    return ClassLoader.class;
                }
                else
                {
                    try
                    {
                        return context.getClassLoader().loadClass(typeName);
                    }
                    catch (ClassNotFoundException classNotFoundException)
                    {
                        logger.config(String.format("WARNING: The specified type %s in %s is can't be loaded.",
                            typeName, xmlElement));

                        return new ConfigurationException(
                            String.format("The specified type %s in %s is can't be loaded.", typeName, xmlElement),
                            "Please ensure the type name is correctly specified and the type is available from the class path",
                            classNotFoundException);
                    }
                }
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object onUnknownElement(ConfigurationContext context,
                                   QualifiedName qualifiedName,
                                   XmlElement xmlElement) throws ConfigurationException
    {
        //ensure that the unknown element is part of the known Coherence elements
        if (!definedElementNames.contains(qualifiedName.getLocalName()))
        {
            logger.config(String.format("WARNING: The element %s in %s is unknown to Coherence.",
                qualifiedName.toString(), xmlElement));
        }

        //when an element is unknown we just process/ignore it as Coherence is non-strict.
        return context.processElementsOf(xmlElement);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartScope(ConfigurationContext context,
                             String prefix,
                             URI uri)
    {
        super.onStartScope(context, prefix, uri);

        //attempt to load and parse the cache-config.dtd element definitions so that we can check for unknown elements
        try
        {
            if (logger.isLoggable(Level.FINER))
            {
                logger.log(Level.FINE,
                    "Attempting to load the Coherence cache-config.dtd to validate XML element names.");
            }

            URL url = Resources.findFileOrResource("cache-config.dtd", context.getClassLoader());

            if (logger.isLoggable(Level.FINER))
            {
                logger.log(Level.FINE, "Loading Coherence cache-config.dtd from " + url);
            }

            if (url == null)
            {
                logger
                    .config("WARNING: Failed to load Coherence cache-config.dtd. Provided configuration XML element names will not be validated.");
            }
            else
            {
                //load element definitions from the url
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.startsWith("<!ELEMENT"))
                    {
                        String element = line.substring(9).trim();

                        int spaceIndex = element.indexOf(" ");
                        if (spaceIndex >= 0)
                        {
                            element = element.substring(0, spaceIndex).trim();
                        }

                        definedElementNames.add(element);
                    }
                }

                reader.close();
            }
        }
        catch (Exception exception)
        {
            logger
                .log(
                    Level.CONFIG,
                    "WARNING: Failed to load Coherence cache-config.dtd. Provided configuration XML element names will not be validated.",
                    exception);
        }

        //register the CacheMappings with the Environment
        context.getEnvironment().registerResource(CacheMappingRegistry.class, new CacheMappingRegistry());
    }


    /**
     * <p>Builds a {@link String} representing an xml coherence cache configuration that has been resolved.</p>
     * 
     * @param builder the {@link StringBuilder} to use
     */
    public void build(StringBuilder builder)
    {
        //include the xml namespace declarations for the cache-config element
        builder.append("<cache-config>\n");

        if (!defaults.isEmpty())
        {
            builder.append("   <defaults>\n");
            for (String name : defaults.keySet())
            {
                build(builder, defaults.get(name), 2);
            }
            builder.append("   </defaults>\n");
        }

        builder.append("   <caching-scheme-mapping>\n");
        for (String cacheName : cacheSchemeMappings.keySet())
        {
            build(builder, cacheSchemeMappings.get(cacheName), 2);
        }
        builder.append("   </caching-scheme-mapping>\n");

        builder.append("\n");
        builder.append("   <caching-schemes>\n");
        for (String schemeName : cachingSchemes.keySet())
        {
            build(builder, cachingSchemes.get(schemeName), 2);
        }
        builder.append("   </caching-schemes>\n");

        builder.append("</cache-config>\n");
    }


    /**
     * <p>Adds a string representation of the specified {@link XmlElement} to the builder that has been resolved.</p>
     * 
     * @param builder    The {@link StringBuilder} to use
     * @param xmlElement The {@link XmlElement} to build a string out of
     * @param indent     The number of tabs to use in the string representation
     */
    @SuppressWarnings("unchecked")
    private void build(StringBuilder builder,
                       XmlElement xmlElement,
                       int indent)
    {
        String padding = Base.dup(' ', indent * 4);
        builder.append(padding);

        builder.append("<");
        builder.append(xmlElement.getName());
        for (String attributeName : ((Map<String, XmlValue>) xmlElement.getAttributeMap()).keySet())
        {
            builder.append(" ");
            builder.append(attributeName);
            builder.append("=\"");
            builder.append(xmlElement.getAttribute(attributeName).toString());
            builder.append("\"");
        }

        if (xmlElement.getString("").trim().length() > 0)
        {
            builder.append(">");
            builder.append(xmlElement.getString("").trim());
            builder.append("</");
            builder.append(xmlElement.getName());
            builder.append(">\n");

        }
        else if (xmlElement.getElementList().size() == 0 && xmlElement.getString("").trim().length() == 0)
        {
            builder.append("/>\n");

        }
        else
        {
            builder.append(">\n");
            for (XmlElement xml : (List<XmlElement>) xmlElement.getElementList())
            {
                build(builder, xml, indent + 1);
            }
            if (xmlElement.getString("").length() == 0)
                builder.append(padding);
            builder.append("</");
            builder.append(xmlElement.getName());
            builder.append(">\n");
        }
    }


    /**
     * <p>Determines if the specified cacheName is defined in the cache scheme mappings.</p>
     * 
     * <p>NOTE: Does not support checking for wildcard mappings.  Just regular string matching (equals) is used.</p>
     * 
     * @param cacheName The name of the cache for with to search.
     * 
     * @return <code>true</code> if the cache name exists in the cache scheme mappings, <code>false</code> otherwise.
     */
    public boolean isCacheNameDefined(String cacheName)
    {
        return cacheSchemeMappings.containsKey(cacheName);
    }


    /**
     * <p>Determines if a scheme with the specified schemeName is defined.</p>
     * 
     * <p>NOTE: Does not support checking for wildcard mappings.  Just regular string matching (equals) is used.</p>
     * 
     * @param schemeName The name of the scheme for with to search.
     * 
     * @return <code>true</code> if the scheme name exists in the defined schemes, <code>false</code> otherwise.
     */
    public boolean isSchemeNameDefined(String schemeName)
    {
        return cachingSchemes.containsKey(schemeName);
    }


    /**
     * <p>Determines if an xml element name is known to this {@link CoherenceNamespaceContentHandler}.</p>
     * 
     * @param xmlElementName The name of the xml configuration element to check
     * 
     * @return <code>true</code> if the xmlElementName is known to this {@link CoherenceNamespaceContentHandler}.
     */
    public boolean isElementDefined(String xmlElementName)
    {
        return definedElementNames.contains(xmlElementName);
    }

}
