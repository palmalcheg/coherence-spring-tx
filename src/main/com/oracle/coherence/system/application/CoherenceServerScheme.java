/*
 * File: CacheServerProcessScheme.java
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

/**
 * A {@link CoherenceServerScheme} is a specialized {@link AbstractJavaApplicationScheme} that's designed
 * explicitly for realizing Coherence DefaultCacheServers.
 *
 * @author Brian Oliver
 */
public class CoherenceServerScheme extends AbstractJavaApplicationScheme<JavaApplication, CoherenceServerScheme>
{

    public static final String DEFAULT_CACHE_SERVER_CLASSNAME = "com.tangosol.net.DefaultCacheServer";

    public static final String PROPERTY_CACHECONFIG = "tangosol.coherence.cacheconfig";

    public static final String PROPERTY_CLUSTER_NAME = "tangosol.coherence.cluster";

    public static final String PROPERTY_CLUSTER_PORT = "tangosol.coherence.clusterport";

    public static final String PROPERTY_DISTRIBUTED_LOCALSTORAGE = "tangosol.coherence.distributed.localstorage";

    public static final String PROPERTY_LOCALHOST_ADDRESS = "tangosol.coherence.localhost";

    public static final String PROPERTY_LOG_LEVEL = "tangosol.coherence.log.level";

    public static final String PROPERTY_SITE_NAME = "tangosol.coherence.site";

    public static final String PROPERTY_TCMP_ENABLED = "tangosol.coherence.tcmp.enabled";

    public static final String PROPERTY_MULTICAST_TTL = "tangosol.coherence.ttl";

    public static final String PROPERTY_POF_CONFIG = "tangosol.pof.config";


    public CoherenceServerScheme()
    {
        super(DEFAULT_CACHE_SERVER_CLASSNAME);
    }


    public CoherenceServerScheme(String classPath)
    {
        super(DEFAULT_CACHE_SERVER_CLASSNAME, classPath);
    }


    public CoherenceServerScheme withCacheConfigURI(String cacheConfigURI)
    {
        withSystemProperty(PROPERTY_CACHECONFIG, cacheConfigURI);
        return this;
    }


    public CoherenceServerScheme withStorageEnabled(boolean isStorageEnabled)
    {
        withSystemProperty(PROPERTY_DISTRIBUTED_LOCALSTORAGE, isStorageEnabled);
        return this;
    }


    public CoherenceServerScheme withTCMPEnabled(boolean isTCMPEnabled)
    {
        withSystemProperty(PROPERTY_TCMP_ENABLED, isTCMPEnabled);
        return this;
    }


    public CoherenceServerScheme withClusterPort(int port)
    {
        withSystemProperty(PROPERTY_CLUSTER_PORT, port);
        return this;
    }


    public CoherenceServerScheme withClusterName(String name)
    {
        withSystemProperty(PROPERTY_CLUSTER_NAME, name);
        return this;
    }


    public CoherenceServerScheme withSiteName(String name)
    {
        withSystemProperty(PROPERTY_SITE_NAME, name);
        return this;
    }


    public CoherenceServerScheme withMulticastTTL(int ttl)
    {
        withSystemProperty(PROPERTY_MULTICAST_TTL, ttl);
        return this;
    }


    public CoherenceServerScheme withLocalHostAddress(String localHostAddress)
    {
        withSystemProperty(PROPERTY_LOCALHOST_ADDRESS, localHostAddress);
        return this;
    }


    public CoherenceServerScheme withLogLevel(int level)
    {
        withSystemProperty(PROPERTY_LOG_LEVEL, level);
        return this;
    }


    public CoherenceServerScheme withPofConfigURI(String pofConfigURI)
    {
        withSystemProperty(PROPERTY_POF_CONFIG, pofConfigURI);
        return this;
    }


    public JavaApplication realize() throws IOException
    {
        return realize(null);
    }
}
