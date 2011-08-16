/*
 * File: DefaultConfigurationContext.java
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
package com.oracle.coherence.environment.extensible;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.environment.Environment;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;
import com.tangosol.run.xml.XmlValue;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.UUID;

/**
 * <p>The default implementation of a {@link ConfigurationContext}.</p>
 *
 * @author Brian Oliver
 */
public class DefaultConfigurationContext implements ConfigurationContext
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static final Logger logger = Logger.getLogger(DefaultConfigurationContext.class.getName());

    /**
     * <p>The {@link Environment} that owns this {@link ConfigurationContext}.</p>
     */
    private Environment environment;

    /**
     * <p>The stack of {@link Scope}s, each of which maintains a collection of
     * registered {@link NamespaceContentHandler}s.  The top of the stack is the
     * first element of the {@link LinkedList}.</p>
     */
    private LinkedList<Scope> scopes;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param environment The {@link Environment} that owns this {@link ConfigurationContext}.
     */
    public DefaultConfigurationContext(Environment environment)
    {
        this.environment = environment;
        this.scopes = new LinkedList<Scope>();
        this.scopes.addFirst(new Scope(this, null));
    }


    /**
     * {@inheritDoc}
     */
    public NamespaceContentHandler ensureNamespaceContentHandler(String prefix,
                                                                 URI uri)
    {
        // find an existing NamespaceContentHandler exist for the uri
        NamespaceContentHandler namespaceContentHandler = getNamespaceContentHandler(uri);

        if (namespaceContentHandler == null)
        {
            // establish a NamespaceContentHandler for the prefix and URI in the current scope
            namespaceContentHandler = getCurrentScope().establishNamespaceContentHandlerFor(prefix, uri);
        }

        return namespaceContentHandler;
    }


    /**
     * {@inheritDoc}
     */
    public Environment getEnvironment()
    {
        return environment;
    }


    /**
     * {@inheritDoc}
     */
    public NamespaceContentHandler getNamespaceContentHandler(String prefix)
    {
        NamespaceContentHandler namespaceContentHandler = null;
        for (Iterator<Scope> scopeIterator = scopes.iterator(); scopeIterator.hasNext()
                && namespaceContentHandler == null;)
        {
            Scope scope = scopeIterator.next();
            URI uri = scope.getNamespaceURI(prefix);
            namespaceContentHandler = uri == null ? null : getNamespaceContentHandler(uri);
        }
        return namespaceContentHandler;
    }


    /**
     * {@inheritDoc}
     */
    public NamespaceContentHandler getNamespaceContentHandler(URI uri)
    {
        NamespaceContentHandler namespaceContentHandler = null;
        for (Iterator<Scope> scopeIterator = scopes.iterator(); scopeIterator.hasNext()
                && namespaceContentHandler == null;)
        {
            Scope scope = scopeIterator.next();
            namespaceContentHandler = scope.getNamespaceContentHandler(uri);
        }
        return namespaceContentHandler;
    }


    /**
     * {@inheritDoc}
     */
    public URI getNamespaceURI(String prefix)
    {
        return getCurrentScope().getNamespaceURI(prefix);
    }


    /**
     * {@inheritDoc}
     */
    public ClassLoader getClassLoader()
    {
        return getEnvironment().getClassLoader();
    }


    /**
     * <p>Returns the current scope (the top of the stack).</p>
     * 
     * @return A {@link Scope} (which is the top of the stack).
     */
    private Scope getCurrentScope()
    {
        return this.scopes.getFirst();
    }


    /**
     * <p>Pushes a new empty scope onto the scopes stack so that newly encountered namespaces 
     * are managed in a new scope, thus overriding previously defined namespaces.</p>
     * 
     * @param xmlElement The {@link XmlElement} for which we are starting a new scope. (may be null)
     */
    private void startScope(XmlElement xmlElement)
    {
        this.scopes.addFirst(new Scope(this, xmlElement));
    }


    /**
     * <p>Terminates the current scope (cleans up it's {@link NamespaceContentHandler}s)
     * and removes it from the stack.</p>
     */
    private void endScope()
    {
        getCurrentScope().terminate();
        this.scopes.removeFirst();
    }


    /**
     * {@inheritDoc}
     */
    public Object processDocument(XmlElement xmlElement) throws ConfigurationException
    {
        //replace all of the "system-property" uses in the document root
        XmlHelper.replaceSystemProperties(xmlElement, "system-property");

        //now process the document root element
        return processElement(xmlElement);
    }


    /**
     * {@inheritDoc}
     */
    public Object processDocument(String xml) throws ConfigurationException
    {
        return processDocument(XmlHelper.loadXml(xml));
    }


    /**
     * {@inheritDoc}
     */
    public Object processDocumentAt(URI uri) throws ConfigurationException
    {
        return processDocument(DefaultConfigurableCacheFactory.loadConfigAsResource(uri.toString(), getClassLoader()));
    }


    /**
     * {@inheritDoc}
     */
    public Object processDocumentAt(String location) throws ConfigurationException
    {
        return processDocument(DefaultConfigurableCacheFactory.loadConfigAsResource(location, getClassLoader()));
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object processElement(XmlElement xmlElement) throws ConfigurationException
    {
        // start a new namespace scope as we're processing a new xmlElement
        startScope(xmlElement);

        // ensure that we have NamespaceContentHandlers in the for each of the attributes 
        //defined in the xmlElement
        for (String attributeName : ((Set<String>) xmlElement.getAttributeMap().keySet()))
        {
            QualifiedName attributeQName = new QualifiedName(attributeName);
            if (attributeQName.getPrefix().equals("xmlns"))
            {
                XmlValue uriValue = xmlElement.getAttribute(attributeName);
                try
                {
                    ensureNamespaceContentHandler(attributeQName.getLocalName(), new URI(uriValue.getString()));
                }
                catch (URISyntaxException uriSyntaxException)
                {
                    throw new RuntimeException(String.format("Invalid URI '%s' specified for XML namespace '%s'",
                        uriValue.getString(), attributeQName.getPrefix()), uriSyntaxException);
                }
            }
        }

        // we need to transform the old "introduce-cache-config" into the appropriate
        // namespace content handler... introduction is no longer hard coded.
        // it's handled by a namespace
        // FUTURE: we perhaps could make the existing namespace content handlers
        // support element transformations!
        QualifiedName elementQName = new QualifiedName(xmlElement);
        if (!elementQName.hasPrefix() && elementQName.getLocalName().equals("introduce-cache-config"))
        {
            logger
                .log(
                    Level.WARNING,
                    "WARNING: The use of <introduce-cache-config file=\"config.xml\" /> "
                            + "is now deprecated. Please use the introduce:config namespace (<introduce:config file=\"config.xml\" />)");
            elementQName = new QualifiedName("introduce", "config");
        }

        // locate an appropriate namespace content handler for the element
        NamespaceContentHandler namespaceContentHandler = getNamespaceContentHandler(elementQName.getPrefix());
        if (namespaceContentHandler == null || !(namespaceContentHandler instanceof ElementContentHandler))
        {
            throw new RuntimeException(
                String
                    .format(
                        "An ElementContentHandler is not available for the element [%s].  Please check that the namespace has been correctly defined",
                        elementQName));
        }

        // process each of the attributes for the element processed using their appropriate attribute content handlers
        Map<String, XmlValue> attributeMap = (Map<String, XmlValue>) (xmlElement.getAttributeMap());
        for (String key : attributeMap.keySet())
        {
            // FUTURE: we should transform the attribute name here as we do above to elements?
            QualifiedName attributeQName = new QualifiedName(key);
            NamespaceContentHandler attributeNamespaceContentHandler = getNamespaceContentHandler(attributeQName
                .getPrefix());

            if (!attributeQName.getPrefix().equals("xmlns") && attributeNamespaceContentHandler == null)
            {
                throw new RuntimeException(
                    String
                        .format(
                            "An AttributeContentHandler is not available for the attribute [%s] in the namespace [%s], in the element [%s].  Please check that the namespace has been correctly defined",
                            attributeQName, attributeQName.getPrefix(), elementQName));
            }
            else
            {
                if (attributeNamespaceContentHandler != null
                        && attributeNamespaceContentHandler instanceof AttributeContentHandler)
                {
                    ((AttributeContentHandler) attributeNamespaceContentHandler).onAttribute(this, attributeQName,
                        attributeMap.get(key));
                }
            }
        }

        // handle the element itself to produce the result
        Object result = ((ElementContentHandler) namespaceContentHandler).onElement(this, elementQName, xmlElement);

        // end the current scope for the element
        endScope();

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public Object processElement(String xml) throws ConfigurationException
    {
        return processElement(XmlHelper.loadXml(xml));
    }


    /**
     * {@inheritDoc}
     */
    public Object processOnlyElementOf(XmlElement xmlElement) throws ConfigurationException
    {
        //the xmlElement must only have a single child
        if (xmlElement.getElementList().size() == 1)
        {
            //determine the scheme from the child element
            return this.processElement((XmlElement) xmlElement.getElementList().get(0));
        }
        else
        {
            //expected only a single element in custom-provider
            throw new ConfigurationException(String.format("Only a single element is permitted in the %s element.",
                xmlElement), String.format("Please consult the documentation regarding use of the '%s' namespace",
                new QualifiedName(xmlElement).getPrefix()));
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T processElementOf(XmlElement xmlElement,
                                  QualifiedName qualifiedName,
                                  T defaultValue) throws ConfigurationException
    {
        //find the child element using the qualified name
        XmlElement childElement = xmlElement.getElement(qualifiedName.toString());
        T result;
        if (childElement == null)
        {
            //no such element, so use the default
            result = defaultValue;

            if (logger.isLoggable(Level.CONFIG))
            {
                logger.config(String.format("Using the default value of [%s] for the xml element <%s> in element <%s>",
                    defaultValue, qualifiedName.getName(), xmlElement.getName()));
            }
        }
        else
        {
            result = (T) processElement(childElement);
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T processElementOf(XmlElement xmlElement,
                                  String id,
                                  T defaultValue) throws ConfigurationException
    {
        //find the element with the specified id
        XmlElement childElement = null;
        for (Iterator<XmlElement> children = (Iterator<XmlElement>) xmlElement.getElementList().iterator(); children
            .hasNext()
                && childElement == null;)
        {
            XmlElement anElement = children.next();
            XmlValue idXmlValue = anElement.getAttribute("id");
            if (idXmlValue != null && idXmlValue.getString().equals(id))
            {
                childElement = anElement;
            }
        }

        T result;
        if (childElement == null)
        {
            //no such element, so use the default
            result = defaultValue;

            if (logger.isLoggable(Level.CONFIG))
            {
                logger.config(String.format(
                    "Using the default value of [%s] for the xml element with id='%s' in element <%s>", defaultValue,
                    id, xmlElement.getName()));
            }
        }
        else
        {
            result = (T) processElement(childElement);
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, ?> processElementsOf(XmlElement xmlElement) throws ConfigurationException
    {
        //process all of the children of the xmlElement
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        for (Iterator<XmlElement> children = (Iterator<XmlElement>) xmlElement.getElementList().iterator(); children
            .hasNext();)
        {
            XmlElement childElement = children.next();
            String id = childElement.getAttributeMap().containsKey("id")
                    ? childElement.getAttribute("id").getString() : new UUID().toString();
            if (id.trim().length() == 0)
            {
                id = new UUID().toString();
            }

            result.put(id, processElement(childElement));
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String result = "DefaultConfigurationContext{\n";

        for (Scope scope : scopes)
        {
            result += scope + "\n";
        }

        return result + "}";
    }


    /**
     * <p>A {@link Scope} maintains the currently defined namespace prefixes and associated 
     * {@link NamespaceContentHandler}s for an {@link XmlElement} that is being processed.</p>
     */
    private class Scope
    {

        /**
         * <p>The {@link ConfigurationContext} in which the {@link Scope} exists.</p>
         */
        private ConfigurationContext context;

        /**
         * <p>The {@link XmlElement} for which this scope was created. (may be null).</p>
         */
        private XmlElement xmlElement;

        /**
         * <p>The xml namespaces registered in this {@link Scope}.  A mapping from prefix to URIs</p>
         */
        private HashMap<String, URI> namespaces;

        /**
         * <p>The {@link NamespaceContentHandler}s for the xml namespaces registered in this {@link Scope}.</p>
         */
        private HashMap<URI, NamespaceContentHandler> namespaceContentHandlers;


        /**
         * <p>Standard Constructor.</p>
         * 
         * @param context The {@link ConfigurationContext} that owns the {@link Scope}
         * @param xmlElement The {@link XmlElement} for which this {@link Scope} was created. (may be null)
         */
        Scope(ConfigurationContext context,
              XmlElement xmlElement)
        {
            this.context = context;
            this.xmlElement = xmlElement;
            this.namespaces = new HashMap<String, URI>();
            this.namespaceContentHandlers = new HashMap<URI, NamespaceContentHandler>();
        }


        /**
         * <p>Returns the {@link XmlElement} for which the {@link Scope} was created.</p>
         * 
         * @return An {@link XmlElement} or <code>null</code>
         */
        XmlElement getXmlElement()
        {
            return xmlElement;
        }


        /**
         * <p>Determines the {@link URI} of the specified prefix registered in this {@link Scope}.</p>
         * 
         * @param prefix The prefix of the {@link URI} to determine.
         * 
         * @return The {@link URI} of the registered prefix or <code>null</code> if none defined in the {@link Scope}.
         */
        URI getNamespaceURI(String prefix)
        {
            return namespaces.get(prefix);
        }


        /**
         * <p>Determines the {@link NamespaceContentHandler} for the {@link URI} registered in the {@link Scope}.
         * 
         * @param uri The {@link URI} of the {@link NamespaceContentHandler} to determine.
         * 
         * @return The {@link NamespaceContentHandler} of the {@link URI} or <code>null</code> if none registered.
         */
        NamespaceContentHandler getNamespaceContentHandler(URI uri)
        {
            return namespaceContentHandlers.get(uri);
        }


        /**
         * <p>Establishes (including loading) an appropriate {@link NamespaceContentHandler}
         * for the specified prefix and {@link URI}.</p>
         * 
         * @param prefix The prefix used to declare the namespace 
         * @param uri The {@link URI} of which we need to establish a {@link NamespaceContentHandler}.
         * 
         * @return The {@link NamespaceContentHandler} for the specified {@link URI}.
         */
        NamespaceContentHandler establishNamespaceContentHandlerFor(String prefix,
                                                                    URI uri)
        {
            // ensure that we don't already have a definition for the prefix in this scope
            if (namespaces.containsKey(prefix))
            {
                throw new RuntimeException(String.format(
                    "Duplicate definition for the namespace prefix [%s] encountered with URI [%s]\n", prefix, uri));
            }
            else if (uri.getScheme().equals("class"))
            {
                String fqcn = uri.getHost() == null ? uri.getSchemeSpecificPart() : uri.getHost();

                try
                {
                    Class<?> clazz = ExternalizableHelper.loadClass(fqcn, context.getClass().getClassLoader(), null);

                    // ensure that the class is a NamespaceHandler
                    if (NamespaceContentHandler.class.isAssignableFrom(clazz))
                    {
                        try
                        {
                            // instantiate and initialize the namespace handler
                            NamespaceContentHandler namespaceHandler = (NamespaceContentHandler) clazz.newInstance();

                            // tell the namespace about the context and it's uri
                            namespaceHandler.onStartScope(context, prefix, uri);

                            // register the NamespaceContentHandler with this scope
                            namespaceContentHandlers.put(uri, namespaceHandler);

                            // register the prefix for the namespace
                            namespaces.put(prefix, uri);

                            return namespaceHandler;
                        }
                        catch (Exception exception)
                        {
                            throw new RuntimeException(String.format(
                                "Can't instantiate the NamespaceContentHandler [%s]\n", fqcn), exception);
                        }
                    }
                    else
                    {
                        throw new RuntimeException(String.format(
                            "The declared NamespaceContentHandler [%s] does not implement the %s interface\n", fqcn,
                            NamespaceContentHandler.class.getName()));
                    }
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    throw new RuntimeException(String.format(
                        "Can't instantiate the NamespaceContentHandler [%s] as the class is not found\n", fqcn),
                        classNotFoundException);
                }
            }
            else
            {
                throw new RuntimeException(String.format(
                    "Can't create a NamespaceContentHandler as the URI scheme [%s] in [%s] is not supported\n", uri
                        .getScheme(), uri));
            }
        }


        /**
         * <p>Terminates and end's all of the {@link NamespaceContentHandler}s established in the {@link Scope}.</p>
         */
        void terminate()
        {
            // end each of the namespace handlers defined in this scope
            for (String prefix : namespaces.keySet())
            {
                URI uri = namespaces.get(prefix);
                namespaceContentHandlers.get(uri).onEndScope(context, prefix, uri);
            }
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            String result = "";
            result += "Scope<" + (getXmlElement() == null ? "root" : xmlElement.getName()) + ">{";

            if (!namespaces.isEmpty())
            {
                result += "namespaces=" + namespaces;
            }

            if (!namespaceContentHandlers.isEmpty())
            {
                result += "handlers=" + namespaceContentHandlers;
            }

            return result + "}";
        }
    }
}
