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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');

        Enumeration<URL> resources = classLoader.getResources(path);

        List<String> dirs = new ArrayList<String>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(URLDecoder.decode(resource.getFile(), "UTF-8"));
        }

        TreeSet<String> classes = new TreeSet<String>();
        for (String directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        ArrayList<Class> classList = new ArrayList<Class>();
        for (String clazz : classes) {
            classList.add(Class.forName(clazz));
        }

        return classList;
    }

    private static TreeSet<String> findClasses(String path, String packageName) throws MalformedURLException, IOException {

        TreeSet<String> classes = new TreeSet<String>();

        // if (path.startsWith("file:") && path.contains("!")) {
        if (path.contains(".jar")) {
            String jarFile = path.substring(0, path.indexOf(".jar") + 4);
            ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
                    if (className.startsWith(packageName)) {
                        classes.add(className);
                    }
                }
            }
            zip.close();

        } else {

            File dir = new File(path);
            if (!dir.exists()) {
                return classes;
            }
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        assert !file.getName().contains(".");
                        classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
                    } else if (file.getName().endsWith(".class")) {
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);

                        classes.add(className);
                    }
                }
            }
        }
        return classes;
    }
}