/*
 * File: EventProcessorFor.java
 * 
 * Copyright (c) 2009-2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.events.processing.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.oracle.coherence.common.events.Event;

/**
 * <p>The {@link EventProcessorFor} annotation is used to declare that
 * a method is used for processing {@link Event}s.</p>
 * 
 * <p>The signature for methods annotated with {@link EventProcessorFor}
 * must be as follows;</p>
 * 
 * <code>public void methodName(EventDispatcher eventDispatcher, Event event);</code>
 *  
 * @author Brian Oliver
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventProcessorFor
{
    /**
     * <p>The {@link Event}s that the annotated method can process.</p> 
     */
    Class<? extends Event>[] events();
}
