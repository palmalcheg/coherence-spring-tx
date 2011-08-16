/*
 * File: AbstractNamedDependencyReference.java
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

/**
 * <p>An abstract implementation of {@link DependencyReference} that has a name.</p>
 * 
 * @author Brian Oliver
 */
public abstract class AbstractNamedDependencyReference implements DependencyReference
{
    /**
     * <p>The name of the dependency.</p>
     */
    private String name;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param name The name of the dependency.
     */
    public AbstractNamedDependencyReference(String name)
    {
        this.name = name;
    }


    /**
     * <p>Returns the name of the dependency being referenced.</p>
     * 
     * @return The {@link String} name of the {@link DependencyReference}.
     */
    public String getName()
    {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractNamedDependencyReference other = (AbstractNamedDependencyReference) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
}
