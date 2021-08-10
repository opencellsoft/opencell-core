package Utils;/*
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

import java.io.File;
import java.net.URL;

public class ResourceUtils {

    public final static String FILE_SEPARATOR = System.getProperty("file.separator");

    public static File getFileFromClasspathResource(String resource) {
        return new File(findFilePathFromClasspathResource(resource));
    }

    public static String findFilePathFromClasspathResource(String resource) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(resource);
        return convertUrlToFilename(url);
    }
    
    public static String convertUrlToFilename(URL url) {
        return convertUrlToFilename(url.getFile()) ;
    }

    public static String convertUrlToFilename(String path) {
        if (path.indexOf("file:/")!=-1) {
            if (path.indexOf(":", path.indexOf("file:/") + 6) != -1)
                path = path.substring(path.indexOf("file:/")+6);
            else
                path = path.substring(path.indexOf("file:/")+5);
        }
        return path.replace('/', System.getProperty("file.separator").charAt(0));
    }
}
