/*
 * File: ReflectionHelper.java
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
package com.oracle.coherence.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>A collection of utilities to assist in using Reflection to create objects.</p>
 * 
 * @author Christer Fahlgren
 * @author Brian Oliver
 */
public class ReflectionHelper
{

    /**
     * Get a compatible constructor to the supplied parameter types.
     * 
     * @param clazz the class which we want to construct
     * @param parameterTypes the types required of the constructor
     * 
     * @return a compatible constructor or null if none exists
     */
    public static Constructor<?> getCompatibleConstructor(Class<?> clazz,
                                                          Class<?>[] parameterTypes)
    {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (int i = 0; i < constructors.length; i++)
        {
            if (constructors[i].getParameterTypes().length == (parameterTypes != null ? parameterTypes.length : 0))
            {
                // If we have the same number of parameters there is a shot that we have a compatible
                // constructor
                Class<?>[] constructorTypes = constructors[i].getParameterTypes();
                boolean isCompatible = true;
                for (int j = 0; j < (parameterTypes != null ? parameterTypes.length : 0); j++)
                {
                    if (!constructorTypes[j].isAssignableFrom(parameterTypes[j]))
                    {
                        // The type is not assignment compatible, however
                        // we might be able to coerce from a basic type to a boxed type
                        if (constructorTypes[j].isPrimitive())
                        {
                            if (!isAssignablePrimitiveToBoxed(constructorTypes[j], parameterTypes[j]))
                            {
                                isCompatible = false;
                                break;
                            }
                        }
                    }
                }
                if (isCompatible)
                {
                    return constructors[i];
                }
            }
        }
        return null;
    }


    /**
     * <p> Checks if a primitive type is assignable with a boxed type.</p>
     * 
     * @param primitive a primitive class type
     * @param boxed     a boxed class type
     * 
     * @return true if primitive and boxed are assignment compatible
     */
    private static boolean isAssignablePrimitiveToBoxed(Class<?> primitive,
                                                        Class<?> boxed)
    {
        if (primitive.equals(java.lang.Boolean.TYPE))
        {
            if (boxed.equals(java.lang.Boolean.class))
                return true;
            else
                return false;
        }
        else
        {
            if (primitive.equals(java.lang.Byte.TYPE))
            {
                if (boxed.equals(java.lang.Byte.class))
                    return true;
                else
                    return false;
            }
            else
            {
                if (primitive.equals(java.lang.Character.TYPE))

                {
                    if (boxed.equals(java.lang.Character.class))
                        return true;
                    else
                        return false;
                }
                else
                {
                    if (primitive.equals(java.lang.Double.TYPE))
                    {
                        if (boxed.equals(java.lang.Double.class))
                            return true;
                        else
                            return false;
                    }
                    else
                    {
                        if (primitive.equals(java.lang.Float.TYPE))
                        {
                            if (boxed.equals(java.lang.Float.class))
                                return true;
                            else
                                return false;
                        }
                        else
                        {
                            if (primitive.equals(java.lang.Integer.TYPE))
                            {
                                if (boxed.equals(java.lang.Integer.class))
                                    return true;
                                else
                                    return false;
                            }
                            else
                            {
                                if (primitive.equals(java.lang.Long.TYPE))
                                {
                                    if (boxed.equals(java.lang.Long.class))
                                        return true;
                                    else
                                        return false;
                                }
                                else
                                {
                                    if (primitive.equals(java.lang.Short.TYPE))
                                    {
                                        if (boxed.equals(java.lang.Short.class))
                                            return true;
                                        else
                                            return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * <p>Create an Object via reflection (using the specified {@link ClassLoader}).</p> 
     * 
     * @param className The name of the class to instantiate.
     * @param classLoader The {@link ClassLoader} to use to load the class.
     * 
     * @return A new instance of the class specified by the className
     * 
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException if there is no such constructor
     * @throws InstantiationException if it failed to instantiate
     * @throws IllegalAccessException if security doesn't allow the call
     * @throws InvocationTargetException if the constructor failed
     */
    public static Object createObject(String className,
                                      ClassLoader classLoader) throws ClassNotFoundException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException,
        InvocationTargetException
    {
        Class<?> clazz = Class.forName(className, true, classLoader);
        Constructor<?> con = clazz.getDeclaredConstructor((Class[]) null);
        return con.newInstance((Object[]) null);
    }


    /**
     * <p>Create an Object via reflection (using the calling Thread context {@link ClassLoader}).</p> 
     * 
     * @param className The name of the class to instantiate.
     * 
     * @return A new instance of the class specified by the className
     * 
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException if there is no such constructor
     * @throws InstantiationException if it failed to instantiate
     * @throws IllegalAccessException if security doesn't allow the call
     * @throws InvocationTargetException if the constructor failed
     */
    @Deprecated
    public static Object createObject(String className) throws ClassNotFoundException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException,
        InvocationTargetException
    {
        return createObject(className, Thread.currentThread().getContextClassLoader());
    }


    /**
     * <p>Create an Object via reflection (using the specified {@link ClassLoader}).</p> 
     * 
     * @param className The name of the class to instantiate.
     * @param constructorParameterList The set of parameters to pass to the constructor
     * @param classLoader The {@link ClassLoader} to use to load the class.
     * 
     * @return A new instance of the class specified by the className
     * 
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException if there is no such constructor
     * @throws InstantiationException if it failed to instantiate
     * @throws IllegalAccessException if security doesn't allow the call
     * @throws InvocationTargetException if the constructor failed
     */
    public static Object createObject(String className,
                                      Object[] constructorParameterList,
                                      ClassLoader classLoader) throws ClassNotFoundException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException,
        InvocationTargetException
    {
        Class<?> clazz = Class.forName(className, true, classLoader);
        Class<?>[] parameterTypes = getClassArrayFromObjectArray(constructorParameterList);
        Constructor<?> con = ReflectionHelper.getCompatibleConstructor(clazz, parameterTypes);
        return con.newInstance(constructorParameterList);
    }


    /**
     * <p>Create an Object via reflection (using the calling Thread context {@link ClassLoader}).</p> 
     * 
     * @param className The name of the class to instantiate.
     * @param constructorParameterList The set of parameters to pass to the constructor
     * 
     * @return A new instance of the class specified by the className
     * 
     * @throws ClassNotFoundException if the class is not found
     * @throws NoSuchMethodException if there is no such constructor
     * @throws InstantiationException if it failed to instantiate
     * @throws IllegalAccessException if security doesn't allow the call
     * @throws InvocationTargetException if the constructor failed
     */
    @Deprecated
    public static Object createObject(String className,
                                      Object[] constructorParameterList) throws ClassNotFoundException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException,
        InvocationTargetException
    {
        return createObject(className, constructorParameterList, Thread.currentThread().getContextClassLoader());
    }


    /**
     * Returns an array of Class objects representing the class of the objects in the parameter.
     * 
     * @param objectArray the array of Objects
     * 
     * @return an array of Classes representing the class of the Objects
     */
    protected static Class<?>[] getClassArrayFromObjectArray(Object[] objectArray)
    {
        Class<?>[] parameterTypes = null;
        if (objectArray != null)
        {
            parameterTypes = new Class[objectArray.length];
            for (int i = 0; i < objectArray.length; i++)
            {
                parameterTypes[i] = objectArray[i].getClass();
            }
        }
        return parameterTypes;
    }
}
