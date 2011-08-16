/*
 * File: CoherenceLogHandler.java
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
package com.oracle.coherence.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.tangosol.net.CacheFactory;
import com.tangosol.util.Base;

/**
 * A {@link CoherenceLogHandler} outputs log statements from the JDK logging package to the Coherence
 * logging facility.
 * 
 * @author Christer Fahlgren 2010.02.03
 */
public class CoherenceLogHandler extends Handler
{

    /**
     * The current log level.
     */
    private Level currentLogLevel;

    /**
     * Standard constructor.
     */
    public CoherenceLogHandler()
    {
        currentLogLevel = determineLogLevel();
    };

    /**
     * Unfortunately, Coherence has no API to determine the log level.
     * @return the {@link Level} that is in effect.
     */
    private Level determineLogLevel()
    {
        if (CacheFactory.isLogEnabled(9))
            return Level.FINEST;
        if (CacheFactory.isLogEnabled(8))
            return Level.FINEST;
        if (CacheFactory.isLogEnabled(7))
            return Level.FINEST;
        if (CacheFactory.isLogEnabled(6))
            return Level.FINER;
        if (CacheFactory.isLogEnabled(5))
            return Level.FINE;
        if (CacheFactory.isLogEnabled(4))
            return Level.CONFIG;
        if (CacheFactory.isLogEnabled(3))
            return Level.INFO;
        if (CacheFactory.isLogEnabled(2))
            return Level.WARNING;
        if (CacheFactory.isLogEnabled(1))
            return Level.SEVERE;
        if (CacheFactory.isLogEnabled(0))
            return Level.ALL;
        return Level.ALL;
    }

    /**
     * Call this method to set this {@link Handler} for the incubator packages.
     */
    public static void initializeIncubatorLogging()
    {
        Logger logger = Logger.getLogger("com.oracle.coherence");
        Handler handler = new CoherenceLogHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        // The handler level is determined by the Coherence log level,
        // setting the logger to the same level
        logger.setLevel(handler.getLevel());
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void publish(LogRecord record)
    {
        // ensure that this log record should be logged by this Handler
        if (!isLoggable(record))
        {
           Level level = getEffectiveLevel(Logger.getLogger(record.getLoggerName()));
           if (level == null)
           {
               return;  
           }
           
           /**
            * A record not loggable by this Handler, with a non-null effective 
            * logging level indicates that the individual incubator class logger
            * level was set independently. Therefore, we set the record logging
            * level to Level.ALL (aka CacheFactory.Log_ALWAYS) to allow the
            * record into the Coherence log stream. 
            */
           record.setLevel(Level.ALL);
        }
    	
        StringBuilder sb = new StringBuilder(record.getMessage());
        sb.append(" Class:");
        sb.append(record.getSourceClassName());
        String msgToLog = null;
        if (record.getParameters() != null)
        {
            msgToLog = MessageFormat.format(sb.toString(), record.getParameters());
        }
        else
        {
            msgToLog = sb.toString();
        }
        
        // Add the throwable stack trace if it exists.
        if (record.getThrown() != null)
        {
            StringWriter stringWriter = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(stringWriter));
            CacheFactory.log(msgToLog + "\n" + stringWriter.toString(), translateLogLevelToCoherence(record.getLevel()));
        }
        else
        {
            CacheFactory.log(msgToLog, translateLogLevelToCoherence(record.getLevel()));
        }
    }

    /**
     * 
     */
    public Level getEffectiveLevel(Logger logger)
    {
        Level effectiveLevel = logger.getLevel();
        if (effectiveLevel == null)
        {
            Level eLevel = getEffectiveLevel(logger.getParent());
            if (eLevel != null)
            {
                effectiveLevel = eLevel;
            }
        }     
        return effectiveLevel;        
    }
    
    /**
     * {@inheritDoc}
     */
    public Level getLevel()
    {
        return currentLogLevel;
    }

    /**
     * Translate from {@link Level} to Coherence log severity.
     * @param level the {@link Level}
     * 
     * @return the Coherence severity
     */
    private int translateLogLevelToCoherence(Level level)
    {
        switch (level.intValue())
        {
            case Integer.MIN_VALUE:
                return Base.LOG_ALWAYS; // Integer.MIN_VALUE = // Level.ALL.intValue()
            case 1000:
                return Base.LOG_ERR;    // 1000 = Level.ERROR.intValue()
            case 900:
                return Base.LOG_WARN;   // 900 = Level.WARNING.intValue()
            case 800:
                return Base.LOG_INFO;   // 800 = Level.INFO.intValue()
            case 700:
                return Base.LOG_INFO;   // 700=Level.CONFIG.intValue()
            case 500:
                return Base.LOG_DEBUG;  // 500 = Level.FINE.intValue()
            case 400:
                return Base.LOG_QUIET;  // 400 = Level.FINER.intValue()
            case 300:
                return Base.LOG_QUIET+1;// 300 = Level.FINEST.intValue():
            case Integer.MAX_VALUE:
                return Base.LOG_MAX;    // Integer.MAX_Value = Level.OFF.intValue():
            default:
                return Base.LOG_ALWAYS;
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public void flush()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws SecurityException
    {
    }
}