/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.commons.utils;

import java.io.File;
import java.net.URL;

public class ResourceUtils {

	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static File getFileFromClasspathResource(String resource) {
		return new File(findBasePathFromClasspathResource(resource), resource);
	}
	
	public static String findBasePathFromClasspathResource(String resource) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(resource);
		
		return findBasePath(convertUrlToFilename(url));
	}

	public static String findBasePath(String path) {
		if(path.indexOf(FILE_SEPARATOR)==-1)
			return path;
		
		return path.substring(0, path.lastIndexOf(FILE_SEPARATOR));
	}
	
	public static String convertUrlToFilename(URL url) {
		return convertUrlToFilename(url.getFile());
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
