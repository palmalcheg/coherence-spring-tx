/*
 * File: ElementContentHandler.java
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

import com.tangosol.run.xml.XmlElement;

/**
 * <p>An {@link ElementContentHandler} is responsible for handling XML element content for a 
 * defined xml namespace.</p>
 *
 * <p>NOTE: Implementations of this interface typically also implement {@link NamespaceContentHandler}.</p>
 *
 * @see NamespaceContentHandler
 * @see AttributeContentHandler
 *
 * @author Brian Oliver
 */
public interface ElementContentHandler
{

    /**
     * <p>Handle when an {@link XmlElement} is encountered in a namespace.</p>
     * 
     * @param context The {@link ConfigurationContext} for the namespace
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement}
     * @param xmlElement The {@link XmlElement} encountered
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     * 
     * @return As part of the processing of the {@link XmlElement}, a value may be returned.  
     *         This value is typically used by parent {@link XmlElement}s.  If not required, return <code>null</code>
     */
    public Object onElement(ConfigurationContext context,
                            QualifiedName qualifiedName,
                            XmlElement xmlElement) throws ConfigurationException;

}
