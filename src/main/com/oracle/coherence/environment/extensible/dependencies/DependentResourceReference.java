/*
 * File: DependentResourceReference.java
 * 
 * Copyright (c) 2010. All Rights Reserved. Oracle Corporation.
 * 
 * Oracle is a registered trademark of Oracle Corporation and/or its
 * affiliates.
 * 
 * This software is the confidential and proprietary information of Oracle
 * Corporation. You shall not disclose such confidential and proprietary
 * information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Oracle Corporation.
 * 
 * Oracle Corporation makes no representations or warranties about 
 * the suitability of the software, either express or implied, 
 * including but not limited to the implied warranties of 
 * merchantability, fitness for a particular purpose, or 
 * non-infringement.  Oracle Corporation shall not be liable for 
 * any damages suffered by licensee as a result of using, modifying 
 * or distributing this software or its derivatives.
 * 
 * This notice may not be removed or altered.
 */

package com.oracle.coherence.environment.extensible.dependencies;

/**
 * <p>A {@link DependentResourceReference} is a {DependencyReference} on another {@link DependentResource}.
 *    enabling the creation of hierarchical dependency chains.</p>
 *
 * @author Christer Fahlgren 2010.02.03
 */
public class DependentResourceReference implements DependencyReference
{

    /**
     * The resource to depend on.
     */
    private DependentResource resource;

    /**
     * Standard constructor.
     * 
     * @param resource the {@link DependentResource} to depend on
     */
    public DependentResourceReference(DependentResource resource)
    {
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferencing(Object object)
    {
        return object != null && object instanceof DependentResource && object == resource;
    }

}
