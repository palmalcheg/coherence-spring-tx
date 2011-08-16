/*
 * File: IntroduceNamespaceContentHandler.java
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

import java.util.HashSet;

import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.NamespaceContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlValue;

/**
 * <p>A {@link NamespaceContentHandler} for the introduction of other Coherence Cache Configuration Files.</p>
 * 
 * <p>Typical use:  &lt;introduce:config file="cache-config.xml"%gt;</p>
 * 
 * <p>NOTE: This implementation has been refactored from the AdvancedConfigurableCacheFactory from
 * version 1.5.x of coherence-common.</p>
 * 
 * @author Brian Oliver
 */
public class IntroduceNamespaceContentHandler extends AbstractNamespaceContentHandler
{

    /**
     * <p>The set of currently introduced cache configuration filenames.</p>
     */
    private HashSet<String> introducedFileNames;


    /**
     * <p>Standard Constructor.</p>
     */
    public IntroduceNamespaceContentHandler()
    {
        this.introducedFileNames = new HashSet<String>();

        registerContentHandler("config", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                XmlValue value = xmlElement.getAttribute("file");
                if (value != null)
                {
                    String fileName = value.getString();
                    if (!introducedFileNames.contains(fileName))
                    {
                        XmlDocument document = DefaultConfigurableCacheFactory.loadConfigAsResource(fileName, context
                            .getClassLoader());
                        introducedFileNames.add(fileName);
                        context.processDocument(document);
                    }
                }
                else
                {
                    throw new ConfigurationException(String.format(
                        "Missing the 'file' attribute from the element [%s].", xmlElement),
                        "Please consult the documentation regarding use of the Introduce namespace");
                }
                return null;
            }
        });
    }
}
