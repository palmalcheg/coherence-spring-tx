/*
 * File: Configurator.java
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
package com.oracle.coherence.configuration;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.util.Value;
import com.oracle.coherence.configuration.expressions.Expression;
import com.oracle.coherence.configuration.expressions.MacroParameterExpression;
import com.oracle.coherence.configuration.parameters.SystemPropertyParameterScope;
import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.oracle.coherence.schemes.ClassScheme;
import com.oracle.coherence.schemes.ReflectiveScheme;
import com.tangosol.run.xml.XmlElement;

/**
 * <p>An {@link Configurator} provides the ability to extract and set annotated {@link Property}s
 * for a {@link Configurable} object from an {@link XmlElement}.</p>
 *
 * @see Configurable
 * @see Property
 *
 * @author Brian Oliver
 */
public class Configurator
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static final Logger logger = Logger.getLogger(Configurator.class.getName());


    /**
     * <p>Sets the annotated {@link Property}s of the {@link Configurable} object with those properties
     * that are available in the provided {@link XmlElement}.</p>
     * 
     * @param object The {@link Configurable} object that requires properties to be set.
     * @param context The {@link ConfigurationContext} that may be used for processing XML.
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement} we're processing.
     * @param xmlElement The {@link XmlElement} containing the properties for the object.
     */
    public static void configure(Object object,
                                 ConfigurationContext context,
                                 QualifiedName qualifiedName,
                                 XmlElement xmlElement) throws ConfigurationException
    {
        if (logger.isLoggable(Level.FINER))
        {
            logger.entering(Configurator.class.getName(), "configure");
        }

        //determine if the @Configurable annotation is present somewhere in the object's class hierarchy or interfaces
        boolean isConfigurable = false;
        Class<?> clazz = object.getClass();
        while (clazz != null && !isConfigurable && clazz != Object.class)
        {
            //is the annotation on the class itself?
            isConfigurable = clazz.isAnnotationPresent(Configurable.class);

            //is it on the interface?
            if (!isConfigurable)
            {
                for (Class<?> interfaceClazz : clazz.getInterfaces())
                {
                    isConfigurable = interfaceClazz.isAnnotationPresent(Configurable.class);

                    if (isConfigurable)
                    {
                        break;
                    }
                }
            }

            if (!isConfigurable)
            {
                clazz = clazz.getSuperclass();
            }
        }

        //ensure that the object is configurable
        if (isConfigurable)
        {
            //use reflection to configure the object
            for (Method method : object.getClass().getMethods())
            {
                //can the method be called to configure a value?
                if (method.isAnnotationPresent(Property.class))
                {
                    //ensure the method has a single parameter
                    if (method.getParameterTypes().length == 1)
                    {
                        //get the property name from the annotation
                        Property property = method.getAnnotation(Property.class);
                        String propertyName = property.value();

                        //is the property defined?
                        if (isPropertyDefined(propertyName, context, qualifiedName, xmlElement))
                        {
                            //determine the type of the property
                            Class<?> propertyType = method.getAnnotation(Type.class) == null ? method
                                .getParameterTypes()[0] : method.getAnnotation(Type.class).value();

                            //get the sub-type for the property
                            Class<?> propertySubType = method.getAnnotation(SubType.class) == null ? null : method
                                .getAnnotation(SubType.class).value();

                            //attempt to get the value for the property
                            Object propertyValue = getMandatoryProperty(propertyName, propertyType, propertySubType,
                                context, qualifiedName, xmlElement);

                            //now attempt to set the property in the object
                            try
                            {
                                method.invoke(object, propertyValue);
                            }
                            catch (Exception exception)
                            {
                                //could not set the property via reflection
                                throw new ConfigurationException(
                                    String.format(
                                        "Could not set the property '%s' using reflection against the annotated method '%s' of '%s'",
                                        propertyName, method, object.getClass().getName()), String.format(
                                        "Please resolve the causing exception.",
                                        context.getNamespaceContentHandler(qualifiedName.getPrefix()).getClass()
                                            .getName()), exception);

                            }
                        }
                        else if (method.getAnnotation(Mandatory.class) != null)
                        {
                            throw new ConfigurationException(
                                String.format("The mandatory property '%s' is not defined in element '%s'.",
                                    propertyName, xmlElement), String.format(
                                    "Please consult the documentation for the use of the %s namespace",
                                    qualifiedName.getPrefix()));
                        }
                        else
                        {
                            if (logger.isLoggable(Level.FINER))
                            {
                                logger
                                    .finer(String
                                        .format(
                                            "The property '%s' does not have a defined value in the element '%s'. Will use the default value.",
                                            propertyName, xmlElement));
                            }
                        }
                    }
                }
            }
        }
        else
        {
            //the object was not @Configurable
            throw new ConfigurationException(String.format(
                "%s is not annotated as being @Configurable.  Consequently it can't be configured declaratively",
                object.getClass().getName()), "Annotate the implementation as being @Configurable.");
        }

        if (logger.isLoggable(Level.FINER))
        {
            logger.exiting(Configurator.class.getName(), "configure");
        }
    }


    /**
     * <p>Determines if the specified property is defined in the {@link XmlElement}.</p>
     * 
     * @param propertyName The name of the property
     * @param context The {@link ConfigurationContext} that may be used for processing XML.
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement}
     * @param xmlElement The {@link XmlElement} in which to search for the property
     * @return <code>true</code> if the property is defined in the {@link XmlElement}, <code>false</code> otherwise.
     * @throws ConfigurationException
     */
    public static boolean isPropertyDefined(String propertyName,
                                            ConfigurationContext context,
                                            QualifiedName qualifiedName,
                                            XmlElement xmlElement) throws ConfigurationException
    {
        //attempt to find an attribute with the specified name
        //when not found, attempt to find an element with the specified property name
        if (xmlElement.getAttribute(propertyName) == null)
        {
            //attempt to process the element with the specified name
            QualifiedName propertyQName = new QualifiedName(qualifiedName.getPrefix(), propertyName);

            //does the element exist (in the current namespace)?
            if (xmlElement.getElement(propertyQName.getName()) == null)
            {

                //does the element exist in the default namespace?
                propertyQName = new QualifiedName("", propertyName);
                return xmlElement.getElement(propertyQName.getName()) != null;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }


    /**
     * <p>Attempts to return the value for the specified property declared within the provided {@link XmlElement}.</p>
     * 
     * <p>PRE-CONDITION: {@link #isPropertyDefined(String, ConfigurationContext, QualifiedName, XmlElement)}.</p>
     * 
     * @param propertyName The name of the property
     * @param propertyType The type of the property
     * @param propertySubType If the propertyType is generic, like a {@link ReflectiveScheme}, the genericType may be used for further type checking.  Set to null if not required.
     * @param context The {@link ConfigurationContext} that may be used for processing XML.
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement} we're processing.
     * @param xmlElement The {@link XmlElement} containing the properties for the object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getMandatoryProperty(String propertyName,
                                             Class<T> propertyType,
                                             Class<?> propertySubType,
                                             ConfigurationContext context,
                                             QualifiedName qualifiedName,
                                             XmlElement xmlElement) throws ConfigurationException
    {
        //attempt to find an attribute with the specified name
        Value value = new Value(xmlElement.getAttribute(propertyName));

        //when not found, attempt to find an element with the specified property name
        if (value.isNull())
        {
            //attempt to process the element with the specified name
            QualifiedName propertyQName = new QualifiedName(qualifiedName.getPrefix(), propertyName);

            //does the element exist (in the current namespace)?
            value = new Value(xmlElement.getElement(propertyQName.getName()));
            if (value.isNull())
            {
                //does the element exist in the default namespace?
                propertyQName = new QualifiedName("", propertyName);
                value = new Value(xmlElement.getElement(propertyQName.getName()));
            }
        }

        if (value.isNull())
        {
            throw new ConfigurationException(String.format(
                "The expected property '%s' is not defined in element '%s'.", propertyName, xmlElement), String.format(
                "Please consult the documentation for the use of the %s namespace", qualifiedName.getPrefix()));
        }
        else
        {
            T propertyValue;

            //can the value be coerced into the required property type automatically
            if (value.hasCoercerFor(propertyType))
            {
                try
                {
                    propertyValue = value.getValue(propertyType);
                }
                catch (ClassCastException classCastException)
                {
                    //the property value could not be coerced into the required type
                    throw new ConfigurationException(
                        String.format(
                            "Incompatible Types: The specified value '%s' for the property '%s' in element '%s' can not be converted into the required property type '%s'.",
                            value.getString(), propertyName, value.getObject(), propertyType),
                        String
                            .format(
                                "Please ensure a correct type of value is specified for the property '%s' in the namespace '%s'.",
                                propertyName, qualifiedName.getPrefix()), classCastException);
                }
            }
            else if (propertyType == Expression.class)
            {
                //FUTURE: in the future we may allow other types of expression and representations
                //        to be "plugged-in" here.  for now we only support Coherence MacroParameters.

                //construct an expression so that it can be evaluated later
                propertyValue = (T) new MacroParameterExpression(propertySubType, value.getString());
            }
            else
            {
                //attempt to resolve the value as an element
                if (value.getObject() instanceof XmlElement)
                {
                    try
                    {
                        //process the element to determine the property value
                        propertyValue = (T) context.processElement((XmlElement) value.getObject());
                    }
                    catch (ConfigurationException configurationException)
                    {
                        //the type of the property is unknown/unsupported
                        throw new ConfigurationException(
                            String.format(
                                "The type of the '%s' property is not supported for declarative configuration.",
                                propertyName),
                            String
                                .format(
                                    "The namespace implementation '%s' will need to programmatically configure the said property.",
                                    context.getNamespaceContentHandler(qualifiedName.getPrefix()).getClass().getName()),
                            configurationException);
                    }

                    //ensure that the property value compatible with the declared property type
                    if (propertyValue != null)
                    {
                        //is the property value compatible with the setter method property type?
                        if (propertyType.isAssignableFrom(propertyValue.getClass()))
                        {
                            //is an annotated property type specified
                            if (propertySubType != null)
                            {
                                //for ReflectiveScheme properties, ensure that the realized value 
                                //is of a compatible type.
                                if ((propertyValue instanceof ReflectiveScheme && !((ReflectiveScheme) propertyValue)
                                    .realizesClassOf(propertySubType))
                                        || (!(propertyValue instanceof ReflectiveScheme) && !propertySubType
                                            .isAssignableFrom(propertyValue.getClass())))
                                {
                                    //the property value does not match with the specified property type
                                    throw new ConfigurationException(
                                        String.format(
                                            "Incompatible Types: The specified value '%s' for the property '%s' in element '%s' is incompatible with the required '%s' property type.",
                                            propertyValue, propertyName, value.getObject(), propertySubType),
                                        String
                                            .format(
                                                "The namespace implementation '%s' will need to programmatically configure the said property.",
                                                context.getNamespaceContentHandler(qualifiedName.getPrefix())
                                                    .getClass().getName()));
                                }
                            }
                        }
                        //is the property value a class scheme that can be realized now to produce the expected type?
                        else if (propertyValue instanceof ClassScheme<?>
                                && ((ClassScheme<?>) propertyValue).realizesClassOf(propertyType))
                        {
                            propertyValue = (T) ((ClassScheme<?>) propertyValue).realize(context.getEnvironment(),
                                context.getClassLoader(), SystemPropertyParameterScope.INSTANCE);
                        }
                        else
                        {
                            //the type of the value does not match the type of the parameter
                            throw new ConfigurationException(
                                String.format(
                                    "Incompatible Types: The specified value '%s' for the property '%s' in element %s is incompatible with the declared type (by the property setter method).",
                                    propertyValue, propertyName, value.getString()),
                                String
                                    .format(
                                        "The namespace implementation '%s' will need to programmatically configure the said property.",
                                        context.getNamespaceContentHandler(qualifiedName.getPrefix()).getClass()
                                            .getName()));
                        }
                    }
                }
                else
                {
                    //the type of the property is unknown/unsupported
                    throw new ConfigurationException(
                        String.format(
                            "The type of the '%s' property for the element '%s' is not supported for declarative configuration.",
                            propertyName, xmlElement),
                        String
                            .format(
                                "The namespace implementation '%s' will need to programmatically configure the said property.",
                                context.getNamespaceContentHandler(qualifiedName.getPrefix()).getClass().getName()));
                }
            }

            return (T) propertyValue;
        }
    }


    /**
     * <p>Attempts to return the value for the specified property declared within the provided {@link XmlElement}.</p>
     * 
     * @param propertyName The name of the property
     * @param propertyType The type of the property
     * @param context The {@link ConfigurationContext} that may be used for processing XML.
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement} we're processing.
     * @param xmlElement The {@link XmlElement} containing the properties for the object.
     */
    public static <T> T getMandatoryProperty(String propertyName,
                                             Class<T> propertyType,
                                             ConfigurationContext context,
                                             QualifiedName qualifiedName,
                                             XmlElement xmlElement) throws ConfigurationException
    {
        return getMandatoryProperty(propertyName, propertyType, null, context, qualifiedName, xmlElement);
    }


    /**
     * <p>Attempts to return the value for the optional property declared within the provided {@link XmlElement}.</p>
     * 
     * @param propertyName The name of the property
     * @param propertyType The type of the property
     * @param propertySubType If the propertyType is generic, like a {@link ReflectiveScheme}, the genericType may be used for further type checking.  Set to {@link Void} if not required.
     * @param defaultValue The returned value if no property is found
     * @param context The {@link ConfigurationContext} that may be used for processing XML.
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement} we're processing.
     * @param xmlElement The {@link XmlElement} containing the properties for the object.
     */
    public static <T> T getOptionalProperty(String propertyName,
                                            Class<T> propertyType,
                                            Class<?> propertySubType,
                                            T defaultValue,
                                            ConfigurationContext context,
                                            QualifiedName qualifiedName,
                                            XmlElement xmlElement) throws ConfigurationException
    {
        if (isPropertyDefined(propertyName, context, qualifiedName, xmlElement))
        {
            return getMandatoryProperty(propertyName, propertyType, propertySubType, context, qualifiedName, xmlElement);
        }
        else
        {
            return defaultValue;
        }
    }


    /**
     * <p>Attempts to return the value for the optional property declared within the provided {@link XmlElement}.</p>
     * 
     * @param propertyName The name of the property
     * @param propertyType The type of the property
     * @param defaultValue The returned value if no property is found
     * @param context The {@link ConfigurationContext} that may be used for processing XML.
     * @param qualifiedName The {@link QualifiedName} of the {@link XmlElement} we're processing.
     * @param xmlElement The {@link XmlElement} containing the properties for the object.
     */
    public static <T> T getOptionalProperty(String propertyName,
                                            Class<T> propertyType,
                                            T defaultValue,
                                            ConfigurationContext context,
                                            QualifiedName qualifiedName,
                                            XmlElement xmlElement) throws ConfigurationException
    {
        if (isPropertyDefined(propertyName, context, qualifiedName, xmlElement))
        {
            return getMandatoryProperty(propertyName, propertyType, context, qualifiedName, xmlElement);
        }
        else
        {
            return defaultValue;
        }
    }
}
