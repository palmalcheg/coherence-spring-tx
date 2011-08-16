/*
 * File: NamespaceContentHandler.java
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

import com.oracle.coherence.environment.extensible.namespaces.AbstractNamespaceContentHandler;

/**
 * <p>A {@link NamespaceContentHandler} is responsible for handling XML content (represented as XML elements and 
 * attributes) belonging to an explicitly declared XML namespace (using xmlns declarations) within a Coherence Cache 
 * Configuration DOM.</p>
 * 
 * <p>For example, the following declares the namespace "mynamespace" to be available for the entire "cache-config" 
 * element with the class "MyNamespaceContentHandler" as the {@link NamespaceContentHandler} that will process content 
 * for the said namespace.</p>
 * <code>
 *  &lt;cache-config xmlns:mynamespace="class://MyNameSpaceContentHandler"&gt
 * </code>
 * 
 * <p>NOTE 1: To allow specific processing of XML elements occurring in the namespace, implementations of this 
 * interface should additionally implement the {@link ElementContentHandler} interface, or alternatively, extend
 * the {@link AbstractNamespaceContentHandler}.</p>
 * 
 * <p>NOTE 2: Likewise, to allow specific processing of XML attributes occurring in the namespace, implementations of 
 * this interface should additionally implement the {@link AttributeContentHandler} interface, or alternatively, extend
 * the {@link AbstractNamespaceContentHandler}.</p>
 * 
 * @see ElementContentHandler
 * @see AttributeContentHandler
 * @see AbstractNamespaceContentHandler
 * 
 * @author Brian Oliver
 * @author Christer Fahlgren
 */
public interface NamespaceContentHandler
{

    /**
     * <p>Handle when the {@link NamespaceContentHandler} is first encountered (declared) in a configuration file, 
     * ie: The beginning of the scope where a namespace can be used.  After this point the 
     * {@link NamespaceContentHandler} will be used to handle XML content declared using the specified prefix.</p>
     * 
     * @param context
     *            The {@link ConfigurationContext} in which the
     *            {@link NamespaceContentHandler} was loaded
     * @param prefix
     *            The prefix that was used to declare the namespace
     * @param uri
     *            The {@link URI} specified for the
     *            {@link NamespaceContentHandler}s
     */
    public void onStartScope(ConfigurationContext context,
                             String prefix,
                             URI uri);


    /**
     * <p>Handle when the {@link NamespaceContentHandler} for a namespace is about to go out of scope in a 
     * configuration file, after which no more XML content for the namespace will be processed.</p>
     * 
     * @param context
     *            The {@link ConfigurationContext} in which the
     *            {@link NamespaceContentHandler} was loaded
     * @param prefix
     *            The prefix that was used to declare the namespace
     * @param uri
     *            The {@link URI} specified for the
     *            {@link NamespaceContentHandler}
     */
    public void onEndScope(ConfigurationContext context,
                           String prefix,
                           URI uri);
}
