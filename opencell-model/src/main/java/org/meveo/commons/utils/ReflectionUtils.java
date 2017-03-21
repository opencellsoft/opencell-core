/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.commons.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reflections.Reflections;
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
            logger.error("Failed to get a list of classes", e);
        }

        return new ArrayList<Class>();
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
            }
        }
        return null;
    }

    /**
     * Remove proxy suffix from a class name. Proxy classes contain a name in "..._$$_javassist.. format" If a proxy class object clasname was passed, strip the ending
     * "_$$_javassist.."to obtain real class name
     * 
     * @param classname Class name
     * @return Class name without a proxy suffix
     */
    public static String getCleanClassName(String classname) {

        int pos = classname.indexOf("_$$_");
        if (pos > 0) {
            classname = classname.substring(0, pos);
            return classname;
        }

        pos = classname.indexOf("$$");
        if (pos > 0) {
            classname = classname.substring(0, pos);
        }

        return classname;
    }

    /**
     * Convert a java type classname to a fuman readable name. E.g. CustomerAccount >> Customer Account
     * 
     * @param classname Full or simple classname
     * @return A humanized class name
     */
    public static String getHumanClassName(String classname) {
        classname = getCleanClassName(classname);
        if (classname.lastIndexOf('.') > 0) {
            classname = classname.substring(classname.lastIndexOf('.') + 1);
        }
        String humanClassname = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(classname), ' ');
        return humanClassname;
    }

    /**
     * Check if object has a field
     * 
     * @param object Object to check
     * @param fieldName Name of a field to check
     * @return True if object has a field
     */
    public static boolean hasField(Object object, String fieldName) {
        if (object == null) {
            return false;
        }
        Field field = FieldUtils.getField(object.getClass(), fieldName, true);
        return field != null;
    }
    
    /**
     * Check if class has a field
     * 
     * @param object Object to check
     * @param fieldName Name of a field to check
     * @return True if object has a field
     */
    public static boolean isClassHasField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return false;
        }
        Field field = FieldUtils.getField(clazz, fieldName, true);
        return field != null;
    }

    public static Class<?> getClassBySimpleNameAndAnnotation(String className, Class<? extends Annotation> annotationClass) {
        Class<?> entityClass = null;
        if (!StringUtils.isBlank(className)) {
            Set<Class<?>> classesWithAnnottation = getClassesAnnotatedWith(annotationClass);
            for (Class<?> clazz : classesWithAnnottation) {
                if (className.equals(clazz.getSimpleName())) {
                    entityClass = clazz;
                    break;
                }
            }
        }
        return entityClass;
    }

    public static Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return getClassesAnnotatedWith(annotationClass, "org.meveo.model");
    }

    public static Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass, String prefix) {
        Reflections reflections = new Reflections(prefix);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotationClass);
        return classes;
    }

    /**
     * Find a class by its simple name that is a subclass of a certain class
     * 
     * @param className Simple classname to match
     * @param parentClass Parent or interface class
     * @return A class object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Class<?> getClassBySimpleNameAndParentClass(String className, Class parentClass) {
        Class<?> entityClass = null;
        if (!StringUtils.isBlank(className)) {
            Reflections reflections = new Reflections("org.meveo");
            if (parentClass.getSimpleName().equals(className)){
                return parentClass;
            }
            Set<Class<?>> classes = reflections.getSubTypesOf(parentClass);
            for (Class<?> clazz : classes) {
                if (className.equals(clazz.getSimpleName())) {
                    entityClass = clazz;
                    break;
                }
            }
        }
        return entityClass;
    }

    /**
     * A check if class represents a DTO or entity class
     * 
     * @param clazz Class to check
     * @return True if class is annotated with @Entity, @Embeddable or @XmlRootElement
     */
    public static boolean isDtoOrEntityType(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class) || clazz.isAnnotationPresent(Embeddable.class) || clazz.isAnnotationPresent(XmlRootElement.class);
    }
}