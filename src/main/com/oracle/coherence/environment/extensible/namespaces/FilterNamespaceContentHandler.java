/*
 * File: FilterNamespaceContentHandler.java
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
import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.oracle.coherence.schemes.ClassScheme;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Filter;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.NeverFilter;

/**
 * <p>The {@link FilterNamespaceContentHandler} provides the ability to declare (and construct) Coherence {@link Filter}
 * instances in a cache configuration file.</p>
 * 
 * @author Brian Oliver
 */
public class FilterNamespaceContentHandler extends AbstractNamespaceContentHandler
{

    /**
     * <p>Standard Constructor.</p> 
     */
    public FilterNamespaceContentHandler()
    {
        registerContentHandler("custom", new ElementContentHandler()
        {

            public Object onElement(ConfigurationContext context,
                                    QualifiedName qualifiedName,
                                    XmlElement xmlElement) throws ConfigurationException
            {
                //TODO: ensure there is only a single element in the publisher-scheme
                Object result = context.processElement((XmlElement) xmlElement.getElementList().get(0));

                if (result != null && result instanceof ClassScheme<?>
                        && ((ClassScheme<?>) result).realizesClassOf(Filter.class))
                {
                    return ((ClassScheme<?>) result).realize(context.getEnvironment(), context.getClassLoader(),
                        SystemPropertyParameterScope.INSTANCE);
                }
                else
                {
                    //the custom filter did not produce a Filter
                    throw new ConfigurationException(String.format(
                        "The filter specified in %s does not implement Filter", xmlElement),
                        "Please consult the documentation regarding use of the Filter namespace");
                }
            }
        });

        registerContentHandler("always", new FilterElementContentHandler(AlwaysFilter.INSTANCE));

        registerContentHandler("never", new FilterElementContentHandler(NeverFilter.INSTANCE));
    }


    /**
     * <p>A {@link FilterElementContentHandler} is a simple {@link ElementContentHandler} for specific
     * types of {@link Filter}s.</p>
     */
    static class FilterElementContentHandler implements ElementContentHandler
    {

        private Filter filter;


        FilterElementContentHandler(Filter filter)
        {
            this.filter = filter;
        }


        public Object onElement(ConfigurationContext context,
                                QualifiedName qualifiedName,
                                XmlElement xmlElement) throws ConfigurationException
        {
            return filter;
        }
    }
}
