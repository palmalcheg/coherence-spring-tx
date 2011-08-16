/*
 * File: ServiceReference.java
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
package com.oracle.coherence.environment.extensible.dependencies;

import com.tangosol.net.Service;

/**
 * <p>An implementation of a {@link DependencyReference} for a named 
 * Coherence {@link Service}s.</p>
 * 
 * @author Brian Oliver
 */
public class ServiceReference extends AbstractNamedDependencyReference
{
    /**
     * <p>Standard Constructor.</p>
     * 
     * @param serviceName The name of the {@link Service} to reference.
     */
    public ServiceReference(String serviceName)
    {
        super(serviceName);
    }


    /**
     * <p>Standard Constructor (for use with {@link Service}s).</p>
     * 
     * @param service The {@link Service} for which to create a reference.
     */
    public ServiceReference(Service service)
    {
        this(service.getInfo().getServiceName());
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReferencing(Object object)
    {
        return object != null && object instanceof Service
                && ((Service) object).getInfo().getServiceName().equals(getName());
    }
}
