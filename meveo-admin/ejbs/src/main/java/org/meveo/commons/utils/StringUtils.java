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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * Utils class for working with strings.
 * 
 * @author Ignas Lelys
 * @created Aug 4, 2010
 */
public class StringUtils {

    /**
     * Checks if string is in array of strings.
     * 
     * @param value
     *            String value to look for.
     * @param stringArray
     *            String array where value is searched.
     * 
     * @return True if array contain string.
     */
    public static boolean isArrayContainString(String value, String[] stringArray) {
        for (int i = 0; i < stringArray.length; i++) {
            if (value != null && value.equals(stringArray[i])) {
                return true;
            }
        }
        return false;
    }

    // TODO test and comment those methods.
    public static boolean isBlank(Object value) {
        return ((value == null) || ((value instanceof String) && ((String) value).trim().length() == 0));
    }

    public static boolean isBlank(String value) {
        return (value == null || value.trim().length() == 0);
    }

    public static String concatenate(String... values) {
        return concatenate(" ", values);
    }

    public static String concatenate(String separator, String[] values) {
        return concatenate(separator, Arrays.asList(values));
    }

    public static String concatenate(String separator, List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (String s : values)
            if (!isBlank(s)) {
                if (sb.length() != 0)
                    sb.append(separator);
                sb.append(s);
            }
        return sb.toString();
    }

    public static String concatenate(Object... values) {
        StringBuilder sb = new StringBuilder();
        for (Object s : values)
            if (!isBlank(s)) {
                if (sb.length() != 0)
                    sb.append(" ");
                sb.append(s);
            }
        return sb.toString();
    }

    public static String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    public static String truncate(String s, int length, boolean indicator) {
        if (isBlank(s) || s.length() <= length)
            return s;

        if (indicator)
            return s.substring(0, length - 3) + "...";
        else
            return s.substring(0, length);
    }

    /**
     * @param value
     * @param nbChar
     * @return
     */
    public static String getStringAsNChar(String value, int nbChar) {
        if (value == null) {
            return null;
        }
        String buildString = value;
        while (buildString.length() < nbChar) {
            buildString = buildString + " ";
        }
        return buildString;
    }

    /**
     * @param value
     * @param nbChar
     * @return
     */
    public static String getLongAsNChar(long value, int nbChar) {
    	String firstChar ="0";
    	if(value < 0){
    		firstChar="-";
    		value = value * -1;
    	}
        String buildString = "" + value;
        while (buildString.length() < nbChar) {
            buildString = "0" + buildString;
        }
        buildString= buildString.replaceFirst("0", firstChar);
        return buildString;
    }

    public static String getArrayElements(String[] t) {
        String str = "";
        for (String s : t) {
            if (str.length() != 0) {
                str += ",";
            }
            str += "'" + s + "'";
        }
        return str;
    }

    public static String concat(Object... values) {
        StringBuilder sb = new StringBuilder();
        for (Object s : values)
            if (!isBlank(s)) {
                sb.append(s);
            }
        return sb.toString();
    }
}
