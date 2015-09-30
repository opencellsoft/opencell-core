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

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    public static String enleverAccent(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
	}
    
	public static String normalizeHierarchyCode(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}

		String newValue = enleverAccent(value) ;

		newValue = newValue.replaceAll("[^A-Za-z0-9]", "_");
		return newValue;
	}
    
    public static String patternMacher(String regex, String text){
    	String result=null;
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(text);
    	if(matcher.find()) {
    		result = matcher.group(1);
    	}
    	return result;
    }
}
