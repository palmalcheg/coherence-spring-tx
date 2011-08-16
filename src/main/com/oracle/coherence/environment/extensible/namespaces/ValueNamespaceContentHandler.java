/*
 * File: ValueNamespaceContentHandler.java
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

import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.tangosol.run.xml.XmlElement;

/**
 * <p>A {@link ValueNamespaceContentHandler} returns type-safe representations of simple values, including; string, 
 * integer, long, boolean, double, float, short, character and byte.</p>
 *
 * @author Christer Fahlgren
 * @author Brian Oliver
 */
public class ValueNamespaceContentHandler extends AbstractNamespaceContentHandler
{
    /**
     * <p>Standard Constructor.</p> 
     */
    public ValueNamespaceContentHandler()
    {
        registerContentHandler("string", new ElementContentHandler()
        {
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return xmlElement.getValue() != null ? xmlElement.getValue().toString() : null;
            }
        });

        registerContentHandler("integer", new ElementContentHandler()
        {
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Integer.parseInt(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });

        registerContentHandler("long", new ElementContentHandler()
        {
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Long.parseLong(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });

        registerContentHandler("boolean", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Boolean.parseBoolean(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });

        registerContentHandler("double", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Double.parseDouble(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });

        registerContentHandler("float", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Float.parseFloat(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });

        registerContentHandler("short", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Short.parseShort(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });

        registerContentHandler("character", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return new Character(xmlElement.getValue() != null ? xmlElement.getValue().toString().charAt(0) : null);
            }
        });

        registerContentHandler("byte", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement)
            {
                return Byte.parseByte(xmlElement.getValue() != null ? xmlElement.getValue().toString() : null);
            }
        });
    }
}
