/*
 * File: AttributeContentHandler.java
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

import com.tangosol.run.xml.XmlValue;

/**
 * <p>An {@link AttributeContentHandler} is responsible for handling XML attribute content
 * for a defined xml namespace.</p>
 *
 * <p>NOTE: Implementations of this interface typically also implement {@link NamespaceContentHandler}.</p>
 *
 * @see NamespaceContentHandler
 * @see ElementContentHandler
 * 
 * @author Brian Oliver
 */
public interface AttributeContentHandler
{

    /**
     * <p>Handle when an XML attribute (represented as an {@link XmlValue}) is encountered for
     * a namespace.</p>
     * 
     * @param context The {@link ConfigurationContext} for the namespace
     * @param qualifiedName The {@link QualifiedName} of the attribute
     * @param xmlValue The {@link XmlValue} representing the attribute
     * 
     * @throws ConfigurationException when a configuration problem was encountered
     */
    public void onAttribute(ConfigurationContext context,
                            QualifiedName qualifiedName,
                            XmlValue xmlValue) throws ConfigurationException;
}
