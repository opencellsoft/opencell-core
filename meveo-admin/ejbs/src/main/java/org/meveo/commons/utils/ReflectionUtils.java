/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.commons.utils;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils class for java reflection api.
 * 
 * @author Ignas Lelys
 * @created 2009.08.05
 */
public class ReflectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * Creates instance from class name.
     * 
     * @param className
     *            Class name for which instance is created.
     * @return Instance of className.
     */
    @SuppressWarnings("rawtypes")
	public static Object createObject(String className) {
        Object object = null;
        try {
            Class classDefinition = Class.forName(className);
            object = classDefinition.newInstance();
        } catch (InstantiationException e) {
            logger.error("Object could not be created by name!", e);
        } catch (IllegalAccessException e) {
            logger.error("Object could not be created by name!", e);
        } catch (ClassNotFoundException e) {
            logger.error("Object could not be created by name!", e);
        }
        return object;
    }

    /**
     * Gets unaccessible private field.
     * 
     * @param clazz
     *            Class of object.
     * @param instance
     *            Object itself.
     * @param fieldName
     *            Private field name.
     * 
     * @return Value of that private field.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @SuppressWarnings("rawtypes")
    public static Object getPrivateField(Class clazz, Object instance, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * Sets unaccessible private field.
     * 
     * @param clazz
     *            Class of object.
     * @param instance
     *            Object itself.
     * @param fieldName
     *            Private field name.
     * @param fieldValue
     *            Private field value to set.
     * 
     * @return Value of that private field.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @SuppressWarnings("rawtypes")
    public static void setPrivateField(Class clazz, Object instance, String fieldName, Object fieldValue)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, fieldValue);
    }

}
