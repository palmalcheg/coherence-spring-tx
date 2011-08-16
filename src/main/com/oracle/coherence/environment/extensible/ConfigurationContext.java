/*
 * File: ConfigurationContext.java
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
import java.util.Map;

import com.oracle.coherence.environment.Environment;
import com.tangosol.run.xml.XmlElement;

/**
 * <p>A {@link ConfigurationContext} provides facilities to aid in the processing 
 * of Coherence Cache Configuration {@link XmlElement}s, including the creation and management
 * of {@link NamespaceContentHandler}s as they are encountered during said processing.</p>
 * 
 * @author Brian Oliver
 */
public interface ConfigurationContext
{

    /**
     * <p>Ensures that a {@link NamespaceContentHandler} with the specified {@link URI} is available for the
     * specified prefix.  If a {@link NamespaceContentHandler} does not already exist, one is established
     * by the {@link ConfigurationContext} and returned.</p>
     * 
     * @param prefix The prefix of the XML Namespace for the {@link NamespaceContentHandler}.
     * @param uri The {@link URI} detailing the location of the {@link NamespaceContentHandler}. Typically this
     *            will be a java class URI, specified as "class://fully.qualified.class.name".
     *            
     * @return An instance of the {@link NamespaceContentHandler} that is suitable for processing the prefix and {@link URI}.
     */
    public NamespaceContentHandler ensureNamespaceContentHandler(String prefix,
                                                                 URI uri);


    /**
     * <p>Locates and returns the {@link NamespaceContentHandler} that is capable of processing the namespace with the 
     * specified prefix.</p>
     *  
     * @param prefix The XML namespace prefix of the {@link NamespaceContentHandler} to locate
     * 
     * @return Returns <code>null</code> if a {@link NamespaceContentHandler} could not be located for the specified prefix.
     */
    public NamespaceContentHandler getNamespaceContentHandler(String prefix);


    /**
     * <p>Locates and returns the current {@link NamespaceContentHandler} that is capable of processing the 
     * namespace defined with the specified {@link URI}.</p>
     *  
     * @param uri The XML namespace {@link URI} of the {@link NamespaceContentHandler} to locate.
     * 
     * @return Returns <code>null</code> if a {@link NamespaceContentHandler} could not be located for the specified {@link URI}.
     */
    public NamespaceContentHandler getNamespaceContentHandler(URI uri);


    /**
     * <p>Locates and returns the {@link URI} that is currently associated with the specified prefix.</p>
     *  
     * @param prefix The XML namespace prefix of the {@link URI} to locate
     * 
     * @return Returns <code>null</code> if a {@link URI} could not be located for the specified {@link URI}.
     */
    public URI getNamespaceURI(String prefix);


    /**
     * <p>Returns the {@link Environment} in which the {@link ConfigurationContext} was defined.</p>
     *  
     * @return The {@link Environment} in which the {@link ConfigurationContext} was defined. 
     */
    public Environment getEnvironment();


    /**
     * <p>Returns the {@link ClassLoader} that should be used to load any classes used by the 
     * {@link ConfigurationContext}</p>
     *  
     * @return The {@link ClassLoader} for the {@link ConfigurationContext}.
     */
    public ClassLoader getClassLoader();


    /**
     * <p>Request that the document specified by the URI/filename (containing the <strong>root<strong> of an XmlDocument) 
     * be processed with appropriate {@link NamespaceContentHandler}s.</p>
     * 
     * <p>NOTE 1: Should the document root contain any unseen xml namespaces, appropriate {@link NamespaceContentHandler}s
     * will be loaded an initialized to process any associated xml elements in the document</p>
     * 
     * <p>NOTE 2: Prior to processing the document root, all of the "system-property"s used in the document
     * <strong>will be replaced</strong> with appropriate System properties.</p>
     * 
     * @param uri The {@link URI} of the XmlDocument to process.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of the {@link ElementContentHandler#onElement(ConfigurationContext, QualifiedName, XmlElement)}
     *         method that handled the XmlDocument 
     */
    public Object processDocumentAt(URI uri) throws ConfigurationException;


    /**
     * <p>Request that the document specified by the URI/filename (containing the <strong>root<strong> of an XmlDocument) 
     * be processed with appropriate {@link NamespaceContentHandler}s.</p>
     * 
     * <p>NOTE 1: Should the document root contain any unseen xml namespaces, appropriate {@link NamespaceContentHandler}s
     * will be loaded an initialized to process any associated xml elements in the document</p>
     * 
     * <p>NOTE 2: Prior to processing the document root, all of the "system-property"s used in the document
     * <strong>will be replaced</strong> with appropriate System properties.</p>
     * 
     * @param location The URI/filename of the XmlDocument to process.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of the {@link ElementContentHandler#onElement(ConfigurationContext, QualifiedName, XmlElement)}
     *         method that handled the XmlDocument 
     */
    public Object processDocumentAt(String location) throws ConfigurationException;


    /**
     * <p>Request that the specified {@link XmlElement} representing the <strong>root<strong> of an XmlDocument 
     * to be processed with appropriate {@link NamespaceContentHandler}s.</p>
     * 
     * <p>NOTE 1: Should the document root contain any unseen xml namespaces, appropriate {@link NamespaceContentHandler}s
     * will be loaded an initialized to process any associated xml elements in the document</p>
     * 
     * <p>NOTE 2: Prior to processing the document root, all of the "system-property"s used in the document
     * <strong>will be replaced</strong> with appropriate System properties.</p>
     * 
     * @param xmlElement The root {@link XmlElement} of the XmlDocument to process.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of the {@link ElementContentHandler#onElement(ConfigurationContext, QualifiedName, XmlElement)}
     *         method that handled the root {@link XmlElement} of the XmlDocument 
     */
    public Object processDocument(XmlElement xmlElement) throws ConfigurationException;


    /**
     * <p>Request that the specified xml string representing the <strong>root<strong> of an xml document 
     * to be processed with appropriate {@link NamespaceContentHandler}s.</p>
     * 
     * <p>NOTE 1: Should the document root contain any unseen xml namespaces, appropriate {@link NamespaceContentHandler}s
     * will be loaded an initialized to process any associated xml elements in the document</p>
     * 
     * <p>NOTE 2: Prior to processing the document root, all of the "system-property"s used in the document
     * <strong>will be replaced</strong> with appropriate System properties.</p>
     * 
     * @param xml A string containing an xml document to process.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of the {@link ElementContentHandler#onElement(ConfigurationContext, QualifiedName, XmlElement)}
     *         method that handled the root xml element in the document 
     */
    public Object processDocument(String xml) throws ConfigurationException;


    /**
     * <p>Request the specified {@link XmlElement} to be processed with an appropriate {@link NamespaceContentHandler} 
     * known by the {@link ConfigurationContext}.</p> 
     * 
     * <p>NOTE: Should the element use any unseen xml namespaces, appropriate {@link NamespaceContentHandler}s
     * will be loaded an initialized to process associated xml element (and child elements).</p>
     * 
     * @param xmlElement The {@link XmlElement} to process
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of the {@link ElementContentHandler#onElement(ConfigurationContext, QualifiedName, XmlElement)}
     *         method that handled the {@link XmlElement}. 
     */
    public Object processElement(XmlElement xmlElement) throws ConfigurationException;


    /**
     * <p>Request the specified xml string be processed with an appropriate {@link NamespaceContentHandler} 
     * known by the {@link ConfigurationContext}.</p> 
     * 
     * <p>NOTE: Should the element use any unseen xml namespaces, appropriate {@link NamespaceContentHandler}s
     * will be loaded an initialized to process associated xml element (and child elements).</p>
     * 
     * @param xml The xml to process
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of the {@link ElementContentHandler#onElement(ConfigurationContext, QualifiedName, XmlElement)}
     *         method that handled the xml element. 
     */
    public Object processElement(String xml) throws ConfigurationException;


    /**
     * <p>Request that the child element (with the {@link QualifiedName}) contained within the {@link XmlElement}
     * be processed using an appropriate {@link NamespaceContentHandler} known by the {@link ConfigurationContext}.</p>
     *  
     * <p>NOTE: If there is more than one child element with the specified {@link QualifiedName}, only the first child
     * encountered is processed.</p>
     *  
     * @param <T> The expected type of value produced by processing the child element. 
     * @param xmlElement The {@link XmlElement} in which the child is defined.
     * @param qualifiedName The {@link QualifiedName} of the child element.
     * @param defaultValue The default value to use if the child element is not located.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of processing the child element or the defaultValue if the child is not in the {@link XmlElement}.
     */
    public <T> T processElementOf(XmlElement xmlElement,
                                  QualifiedName qualifiedName,
                                  T defaultValue) throws ConfigurationException;


    /**
     * <p>Request that the child element (identified by id attribute ie: an attribute where id="...") contained within 
     * the {@link XmlElement} be processed using an appropriate {@link NamespaceContentHandler} known by 
     * the {@link ConfigurationContext}.</p>
     *  
     * @param <T> The expected type of value produced by processing the child element. 
     * @param xmlElement The {@link XmlElement} in which the child is defined.
     * @param id The identity of the child element.  ie: the child element has an id="..." attribute
     * @param defaultValue The default value to use if the child element is not located.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return The result of processing the child element or the defaultValue if the child is not in the {@link XmlElement}.
     */
    public <T> T processElementOf(XmlElement xmlElement,
                                  String id,
                                  T defaultValue) throws ConfigurationException;


    /**
     * <p>Request that the <strong>only</strong> child element contained within the {@link XmlElement} is processed 
     * using an appropriate {@link NamespaceContentHandler} known by the {@link ConfigurationContext}.</p>
     *  
     * @param xmlElement The {@link XmlElement} in which the child is defined.
     * 
     * @throws ConfigurationException when a configuration problem was encountered, 
     *         especially if there is zero or more than one child.
     * 
     * @return The result of processing the child element.
     */
    public Object processOnlyElementOf(XmlElement xmlElement) throws ConfigurationException;


    /**
     * <p>Request that all of the child elements contained within the specified {@link XmlElement} are to be 
     * processed using appropriate {@link NamespaceContentHandler}s known by the {@link ConfigurationContext}.</p>
     * 
     * <p>This is a convenience method to aid in the processing of all children of an {@link XmlElement}. The keys
     * of the returned {@link Map} represent the id attributes each child {@link XmlElement}.  If an {@link XmlElement}
     * does not have a specified id attribute, a UUID is generated in it's place.</p>
     * 
     * @param xmlElement The parent {@link XmlElement} of the children to process.
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     *  
     * @return A {@link Map} from identifiable child {@link XmlElement}s (with id="..." attributes) 
     *         and their corresponding processed values.
     */
    public Map<String, ?> processElementsOf(XmlElement xmlElement) throws ConfigurationException;
}
