/*
 * File: Logger.java
 * 
 * Copyright (c) 2008-2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.logging;

import com.tangosol.net.CacheFactory;

/**
 * <p>A simple wrapper around the Coherence logging framework
 * (to avoid polluting source code with {@link CacheFactory} calls).</p>
 * 
 * <p><strong>NOTICE:</strong> This class may not be supported in the future.  
 * All logging should now use the java.util.logging framework, for which there 
 * is now a {@link CoherenceLogHandler}.</p>
 * 
 * @author Brian Oliver
 */
public class Logger
{
    /**
     * <p>The ALWAYS level constant.</p>
     */
    public static final int ALWAYS = CacheFactory.LOG_ALWAYS;

    /**
     * <p>The ERROR level constant.</p>
     */
    public static final int ERROR = CacheFactory.LOG_ERR;

    /**
     * <p>The WARN level constant.</p>
     */
    public static final int WARN = CacheFactory.LOG_WARN;

    /**
     * <p>The INFO level constant.</p>
     */
    public static final int INFO = CacheFactory.LOG_INFO;

    /**
     * <p>The DEBUG level constant.</p>
     */
    public static final int DEBUG = CacheFactory.LOG_DEBUG;

    /**
     * <p>The QUIET level constant.</p>
     */
    public static final int QUIET = CacheFactory.LOG_QUIET;


    /**
     * <p>Determines if the specified logging severity level is enabled.</p>
     * 
     * @param severity the level to check
     * 
     * @return true if the specified severity level is enabled
     */
    public static final boolean isEnabled(int severity)
    {
        return CacheFactory.isLogEnabled(severity);
    }


    /**
     * <p>Logs the specifically (Java 5+) formatted string at the specified level of severity.</p>
     *  
     * @param severity the severity level to log
     * @param format   the format of the log message
     * @param params   parameters to the formatted log message
     */
    public static void log(int severity,
                           String format,
                           Object... params)
    {
        CacheFactory.log(String.format(format, params), severity);
    }
}
