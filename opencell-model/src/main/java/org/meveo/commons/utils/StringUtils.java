/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.commons.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils class for working with strings.
 * 
 * @author Ignas Lelys
 */
public class StringUtils {

    public static final String CODE_REGEX = "^[ @A-Za-z0-9_\\.\\/-]+$";
    public static final String EMPTY = "";

    /**
     * Checks if string is in array of strings.
     * 
     * @param value String value to look for.
     * @param stringArray String array where value is searched.
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

    /**
     * Value is null or empty
     * 
     * @param value Value to check
     * @return True if value is null or is empty
     */
    public static boolean isBlank(String value) {
        return (value == null || value.trim().isEmpty());
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String concatenate(String... values) {
        return concatenate(" ", values);
    }

    public static String concatenate(String separator, String[] values) {
        return concatenate(separator, Arrays.asList(values));
    }

    @SuppressWarnings("rawtypes")
    public static String concatenate(String separator, Collection values) {
        StringBuilder sb = new StringBuilder();
        for (Object s : values)
            if (!isBlank(s)) {
                if (sb.length() != 0) {
                    sb.append(separator);
                }

                if (s.getClass().isArray()) {
                    sb.append(StringUtils.concatenate(",", (String[]) s));
                } else {
                    sb.append(s);
                }

            }
        return sb.toString();
    }

    public static String concatenate(Object... values) {
        StringBuilder sb = new StringBuilder();
        for (Object s : values) {
            if (!isBlank(s)) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }

    /**
     * @param filePath file path
     * @return content of file as string
     * @throws java.io.IOException input/output exception.
     */
    public static String readFileAsString(String filePath) throws java.io.IOException {
        int bufferSize = 1024;
        StringBuffer fileData = new StringBuffer(1000);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            char[] buf = new char[bufferSize];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[bufferSize];
            }
        } catch (IOException ex) {
            throw ex;
        }

        return fileData.toString();
    }

    /**
     * @param s input string
     * @param length length of string
     * @param indicator true/false
     * @return truncated string.
     */
    public static String truncate(String s, int length, boolean indicator) {
        if (isBlank(s) || s.length() <= length) {
            return s;
        }

        if (indicator) {
            return s.substring(0, length - 3) + "...";
        } else {
            return s.substring(0, length);
        }
    }

    /**
     * @param value input string
     * @param nbChar number of char
     * @return sub string.
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
     * @param value input long value
     * @param nbChar number of char
     * @return long as string
     */
    public static String getLongAsNChar(long value, int nbChar) {
        String firstChar = "0";
        if (value < 0) {
            firstChar = "-";
            value = value * -1;
        }
        String buildString = "" + value;
        while (buildString.length() < nbChar) {
            buildString = "0" + buildString;
        }
        buildString = buildString.replaceFirst("0", firstChar);
        return buildString;
    }

    /**
     * Returns the zero-padded RUM sequence value.
     * 
     * @param value input Long value
     * @param nbChar number of char
     * @return Long as string
     */
    public static String getLongAsNChar(Long value, Long nbChar) {
        String firstChar = "0";
        if (value < 0) {
            firstChar = "-";
            value = value * -1L;
        }
        StringBuilder buildString = new StringBuilder("" + value);
        while (buildString.length() < nbChar) {
            buildString = buildString.insert(0, "0");
        }
        buildString = buildString.replace(0, 1, firstChar);

        return buildString.toString();
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
        for (Object s : values) {
            if (!isBlank(s)) {
                sb.append(s);
            }
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

        String newValue = enleverAccent(value);

        newValue = newValue.replaceAll("[^\\-A-Za-z0-9.@-]", "_");
        return newValue;
    }

    public static String patternMacher(String regex, String text) {
        String result = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * Compares two strings. Handles null values without exception
     * 
     * @param one First string
     * @param two Second string
     * @return Matches String.compare() return value
     */
    public static int compare(String one, String two) {

        if (one == null && two != null) {
            return 1;
        } else if (one != null && two == null) {
            return -1;
        } else if (one == null && two == null) {
            return 0;
        } else if (one != null && two != null) {
            return one.compareTo(two);
        }

        return 0;
    }

    public static boolean isMatch(String value, String regEx) {
        Pattern r = Pattern.compile(regEx);
        Matcher m = r.matcher(value);
        return m.find();
    }

    public static String normalizeFileName(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        String newValue = enleverAccent(value);
        newValue = newValue.replaceAll("[:*?\"<>|]", "");
        newValue = newValue.replaceAll("\\s+", "_");
        return newValue;
    }

    /**
     * Return a value or a default value if empty
     * 
     * @param value Value to check
     * @param defaultValue A default value to return if value is empty
     * @return A value or a default value if empty
     */
    public static final String getDefaultIfEmpty(String value, String defaultValue) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(value) ? value : defaultValue;
    }

    /**
     * Return a value or a default value if null
     * 
     * @param value Value to check
     * @param defaultValue A default value to return if value is null
     * @return A value or a default value if null
     */
    public static final String getDefaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Check if string s contain only digital character
     *
     * @param s
     * @return
     */
    public static boolean isDigital(String s) {

        for (Character c : s.toCharArray()) {

            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}