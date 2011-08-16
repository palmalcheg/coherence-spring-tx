/*
 * File: ApplicationScheme.java
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
 * An {@link ApplicationScheme} is responsible the creation of one or more {@link Application}s.
 *
 * @param <A> The type of {@link Application} that the {@link ApplicationScheme} will create.
 * @param <T> The type of the {@link ApplicationScheme} from which default configuration may be retrieved.
 *
 * @author Brian Oliver
 */
public interface ApplicationScheme<A extends Application, T extends ApplicationScheme<A, ?>>
{

    /**
     * Returns the {@link PropertiesScheme} that will be used to configure the operating system environment
     * variables for the realized {@link Application}.
     * 
     * @return {@link PropertiesScheme}
     */
    public PropertiesScheme getEnvironmentVariablesScheme();


    /**
     * Realizes an instance of an {@link Application}, optionally using the information provided by 
     * parent {@link ApplicationScheme}.
     * 
     * @param defaultScheme An {@link ApplicationScheme} that should be used to determine
     *        default environment variable declarations.  This may be <code>null</code> if not required.
     * 
     * @return An {@link Application} representing the application realized by the {@link ApplicationScheme}.
     * 
     * @throws IOException Thrown if a problem occurs while realizing the application
     */
    public A realize(T defaultScheme) throws IOException;
}
