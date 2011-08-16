/*
 * File: Identifier.java
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
package com.oracle.coherence.common.identifiers;

/**
 * <p>An {@link Identifier} is an object that may be used to uniquely 
 * identify some other object, class, concept, value or subject.</p>
 * 
 * <p>The "life-time" or "validity" of an {@link Identifier} is
 * entirely dependent on the "life-time" of the subject to which
 * the {@link Identifier} is identifying. Meaning, an {@link Identifier}
 * may represent the identity of a subject that has been destroyed, lost
 * or expired.</p>
 * 
 * <p>Likewise the "domain" in which an {@link Identifier} is "unique" is 
 * entirely dependent on the "domain" of the subject to which
 * the {@link Identifier} is identifying.  Meaning, a single
 * {@link Identifier} may actually identify more than one subject
 * when it is used in different domains.</p>
 * 
 * <p>When used with Coherence, {@link Identifier}s are often used as 
 * keys for entries in a cache.  In which case they must always 
 * provide a "correct" implementation of {@link Object#hashCode()} and
 * {@link Object#equals(Object)}, together with a mechanism for serializing
 * and deserializing themselves.</p> 
 * 
 * <p>An {@link Identifier} is essentially a marker interface to signify
 * that some object is used to identify subjects. There are no specific
 * requirements for a subject to be consider identifiable.</p>
 * 
 * @author Brian Oliver
 */
public interface Identifier
{

    /**
     * <p>As this is a marker interface, there are no declarations.</p>
     */
}
