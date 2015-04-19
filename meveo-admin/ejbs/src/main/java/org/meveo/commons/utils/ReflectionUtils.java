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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jfree.util.Log;
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
     * @param className Class name for which instance is created.
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

    @SuppressWarnings("rawtypes")
    public static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Class CL_class = classLoader.getClass();
            while (CL_class != java.lang.ClassLoader.class) {
                CL_class = CL_class.getSuperclass();
            }
            java.lang.reflect.Field ClassLoader_classes_field = CL_class.getDeclaredField("classes");
            ClassLoader_classes_field.setAccessible(true);
            Vector classes = (Vector) ClassLoader_classes_field.get(classLoader);

            ArrayList<Class> classList = new ArrayList<Class>();

            for (Object clazz : classes) {
                if (((Class) clazz).getName().startsWith(packageName)) {
                    classList.add((Class) clazz);
                }
            }

            return classList;

        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            Log.error("Failed to get a list of classes", e);
        }

        return new ArrayList<Class>();
    }
}