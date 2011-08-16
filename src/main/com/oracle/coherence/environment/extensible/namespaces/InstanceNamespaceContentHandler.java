/*
 * File: InstanceNamespaceContentHandler.java
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

import java.util.Iterator;
import java.util.List;

import com.oracle.coherence.common.util.ReflectionHelper;
import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.oracle.coherence.schemes.ClassScheme;
import com.oracle.coherence.schemes.ParameterizedClassScheme;
import com.oracle.coherence.schemes.Scheme;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlValue;

/**
 * <p>The {@link InstanceNamespaceContentHandler} processes an &lt;instance:class&gt {@link XmlElement}
 * to produce a {@link ClassScheme}.</p>
 *
 * @author Christer Fahlgren
 * @author Brian Oliver
 */
public class InstanceNamespaceContentHandler extends AbstractNamespaceContentHandler
{

    /**
     * <p>Standard Constructor.</p>
     */
    public InstanceNamespaceContentHandler()
    {
        registerContentHandler("class", new ElementContentHandler()
        {

            @SuppressWarnings({ "unchecked" })
            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                // attempt to create a Scheme based on the information in the xmlElement

                // find the classname for the instance to create
                if (xmlElement.getAttributeMap().containsKey("classname"))
                {
                    XmlValue value = xmlElement.getAttribute("classname");
                    final String className = value.getString();

                    // create a list of parameters for the constructor of the instance
                    List<XmlElement> elementList = xmlElement.getElementList();
                    final Object[] constructorParameterList = new Object[elementList.size()];
                    Iterator<XmlElement> iter = elementList.iterator();
                    int i = 0;
                    while (iter.hasNext())
                    {
                        XmlElement childElement = iter.next();
                        constructorParameterList[i] = context.processElement(childElement);
                        i++;
                    }

                    try
                    {
                        //determine if the class already is a scheme
                        Class<?> clazz = Class.forName(className, false, context.getClassLoader());

                        if (Scheme.class.isAssignableFrom(clazz))
                        {
                            //already a scheme, so just create an instance and return it
                            return ReflectionHelper.createObject(className, constructorParameterList, context
                                .getClassLoader());
                        }
                        else
                        {
                            //make the specified class and parameters into a class scheme
                            return new ParameterizedClassScheme(className, constructorParameterList);
                        }
                    }
                    catch (Exception exception)
                    {
                        throw new ConfigurationException(String.format(
                            "Failed to instantiate the class '%s' specified by element '%s' with the classloader '%s'",
                            className, xmlElement, context.getClassLoader()),
                            "Please ensure the said class is available", exception);
                    }
                }
                else
                {
                    throw new ConfigurationException(String.format(
                        "The InstanceNamespaceHandler expected a 'classname' attribute in the element [%s].",
                        xmlElement), "Please consult the documentation for correct use of the Instance namespace.");
                }
            }
        });
    }
}
