/*
 * File: EnvironmentNamespaceContentHandler.java
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
package com.oracle.coherence.environment.extensible.namespaces;

import com.oracle.coherence.configuration.parameters.SystemPropertyParameterScope;
import com.oracle.coherence.environment.Environment;
import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.oracle.coherence.schemes.ClassScheme;
import com.tangosol.run.xml.XmlElement;

/**
 * <p>The {@link EnvironmentNamespaceContentHandler} allows for the following configuration options.</p>
 * <ul>
 * <li>
 *      <strong>environment:instance</strong> - returns a reference to the {@link Environment}.
 * </li>
 * <li>
 *      <strong>environment:resource</strong> - registers the object produced by interpreting a child element with the 
 *      supplied class/interface attribute name in the {@link Environment}.
 * </li>
 * <li>
 *      <strong>environment:ref</strong> - provides a reference to an object of a specified interface with in an {@link Environment}.
 * </li>
 * </ul>
 *
 * @author Christer Fahlgren
 * @author Brian Oliver
 */
public class EnvironmentNamespaceContentHandler extends AbstractNamespaceContentHandler
{

    /**
     * <p>Standard Constructor.</p>
     */
    public EnvironmentNamespaceContentHandler()
    {
        registerContentHandler("instance", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                return context.getEnvironment();
            }
        });

        registerContentHandler("resource", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                String name = xmlElement.getAttribute("id").getString();
                XmlElement childElement = (XmlElement) xmlElement.getElementList().get(0);
                if (childElement == null)
                {
                    throw new ConfigurationException(String.format(
                        "Expected child element of [%s] to construct a ClassScheme resource for the environment.",
                        xmlElement), "Please consult the documentation for the Environment namespace");

                }
                else
                {
                    Object objectToRegister = context.processElement(childElement);
                    try
                    {
                        //we need to realize ClassSchemes immediately so that we can register the resource
                        if (objectToRegister instanceof ClassScheme<?>)
                        {
                            objectToRegister = ((ClassScheme<?>) objectToRegister).realize(context.getEnvironment(),
                                context.getClassLoader(), SystemPropertyParameterScope.INSTANCE);
                        }

                        context.getEnvironment().registerResource((Class<?>) Class.forName(name), objectToRegister);
                    }
                    catch (ClassNotFoundException classNotFoundException)
                    {
                        throw new ConfigurationException(String.format(
                            "The class/interface name [%s] specified in the <%s:resource> element is unknown.", name,
                            getPrefix()),
                            "Please ensure the class is on the classpath and/or the classname is correct",
                            classNotFoundException);
                    }
                    return objectToRegister;
                }
            }
        });

        registerContentHandler("ref", new ElementContentHandler()
        {

            @SuppressWarnings("unchecked")
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                String name = xmlElement.getAttribute("id").getString();
                try
                {
                    return context.getEnvironment().getResource((Class<Object>) Class.forName(name));
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    throw new ConfigurationException(String
                        .format("The class/interface name [%s] specified in the <%s:ref> element is unknown", name,
                            getPrefix()),
                        "Please ensure the class is on the classpath and/or the classname is correct",
                        classNotFoundException);
                }
            }
        });
    }
}
