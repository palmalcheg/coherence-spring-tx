/*
 * File: JavaApplicationScheme.java
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

/**
 * A {@link JavaApplicationScheme} is Java specific {@link ApplicationScheme}.
 *
 * @param <A> The type of {@link JavaApplication} that the {@link JavaApplicationScheme} will create.
 * @param <T> The type of the {@link JavaApplicationScheme} from which default configuration may be retrieved.
 *
 * @see JavaApplication
 * @see ApplicationScheme
 * 
 * @author Brian Oliver
 */
public interface JavaApplicationScheme<A extends JavaApplication, T extends JavaApplicationScheme<A, ?>> extends
        ApplicationScheme<A, T>
{

    /**
     * Returns the {@link PropertiesScheme} that will be used as a basis for configuring the Java System Properties
     * of the realized {@link JavaApplication}s from this {@link JavaApplicationScheme}.
     * 
     * @return {@link PropertiesScheme}
     */
    public PropertiesScheme getSystemPropertiesScheme();

}
