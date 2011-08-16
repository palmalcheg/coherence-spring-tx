/*
 * File: AdvancedConfigurableCacheFactory.java
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
package com.oracle.coherence.common.configuration;

import com.oracle.coherence.environment.extensible.ExtensibleEnvironment;
import com.tangosol.run.xml.XmlElement;

/**
 * <p>The purpose of the {@link AdvancedConfigurableCacheFactory} was initially to provide
 * the ability to "introduce" cache configurations from other files to construct a single
 * cache configuration.  This functionality, together with the ability to dynamically handle 
 * new namespaces is now provided by the {@link ExtensibleEnvironment} implementation.</p>
 * 
 * <p>It is strongly advised that developers use {@link ExtensibleEnvironment}s instead
 * of {@link AdvancedConfigurableCacheFactory}s as we're deprecating them.</p>
 * 
 * @author Brian Oliver
 */
@Deprecated
public class AdvancedConfigurableCacheFactory extends ExtensibleEnvironment
{
    /**
     * Constructor.
     */
    public AdvancedConfigurableCacheFactory()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param path   The path to the configuration file
     * @param loader The classloader associated with this condfiguration
     */
    public AdvancedConfigurableCacheFactory(String path,
                                            ClassLoader loader)
    {
        super(path, loader);
    }

    /**
     * Constructor.
     * 
     * @param path The path to the configuration file
     */
    public AdvancedConfigurableCacheFactory(String path)
    {
        super(path);
    }

    /**
     * Constructor. 
     * 
     * @param xmlConfig The XML configuration to use for this cache factory
     */
    public AdvancedConfigurableCacheFactory(XmlElement xmlConfig)
    {
        super(xmlConfig);
    }
}
