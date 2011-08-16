/*
 * File: ConfigurationException.java
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
package com.oracle.coherence.environment.extensible;

/**
 * <p>A {@link ConfigurationException} captures information concerning an invalid configuration of Coherence.  
 * Specifically it details what the problem was and advice on resolving the issue.  Optionally
 * a {@link ConfigurationException} may be include the causing {@link Exception}.</p>
 *
 * @author Brian Oliver 
 */
@SuppressWarnings("serial")
public class ConfigurationException extends Exception
{

    /**
     * <p>The problem that occurred</p>
     */
    private String problem;

    /**
     * <p>Advice for resolving the issue.</p>
     */
    private String advice;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param problem
     * @param advice
     */
    public ConfigurationException(String problem,
                                  String advice)
    {
        this.problem = problem;
        this.advice = advice;
    }


    /**
     * <p>Standard Constructor (with cause).</p>
     * 
     * @param problem
     * @param cause
     */
    public ConfigurationException(String problem,
                                  String advice,
                                  Throwable cause)
    {
        super(cause);
        this.problem = problem;
        this.advice = advice;
    }


    /**
     * <p>Returns what the problem was.</p>
     * 
     * @return A string detailing the problem
     */
    public String getProblem()
    {
        return problem;
    }


    /**
     * <p>Returns advice to resolve the issue.</p>
     * 
     * @return A string detailing advice to resolve the issue
     */
    public String getAdvice()
    {
        return advice;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage()
    {
        String result = "";
        result += "Configuration Exception\n";
        result += "-----------------------\n";
        result += "Problem   : " + getProblem() + "\n";
        result += "Advice    : " + getAdvice() + "\n";

        if (this.getCause() != null)
        {
            result += "Caused By : " + getCause().toString() + "\n";
        }

        return result;
    }
}
