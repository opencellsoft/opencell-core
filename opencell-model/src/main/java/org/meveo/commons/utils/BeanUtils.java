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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object related methods
 * 
 * @author Andrius Karpavicius
 *
 */
public class BeanUtils {

    /**
     * Check that all fields of an object as identical.
     * 
     * @param one First object
     * @param two Second object
     * @param fieldsToOmmit Fields to ignore
     * @return true/false
     */
    public static boolean isIdentical(Object one, Object two, String... fieldsToOmmit) {

        if (one == null && two == null) {
            return true;
        } else if (one == null || two == null) {
            return false;
        }

        List<Field> fields = new ArrayList<>();

        fields = ReflectionUtils.getAllFields(fields, one.getClass());

        for (Field field : fields) {

            try {
                Object valueOne = FieldUtils.readField(field, one, true);
                Object valueTwo = FieldUtils.readField(field, two, true);

                if (compare(valueOne, valueTwo) != 0) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                Logger log = LoggerFactory.getLogger(BeanUtils.class);
                log.error("Failed to determine if two objects are identical", e);
            }
        }

        return true;

    }

    /**
     * Compares two strings. Handles null values without exception
     * 
     * @param one First string
     * @param two Second string
     * @return Matches String.compare() return value
     */
    private static int compare(Object one, Object two) {

        if (one == null && two != null) {
            return 1;
        } else if (one != null && two == null) {
            return -1;
        } else if (one == null && two == null) {
            return 0;
        } else if (one != null && two != null) {
            if (one instanceof String) {
                return ((String) one).compareTo((String) two);
            } else {
                return one.toString().compareTo(two.toString());
            }
        }

        return 0;
    }

    /**
     * Shallow equality check for two objects using the equals operator while handling null values without exception
     *
     * @param one First object
     * @param two Second string
     * @return true if both objects are null or are equal using Object.equals
     */
    public static boolean equals(Object one, Object two) {
        if (one == null && two == null) {
            return true;
        }
        return one != null ? one.equals(two) : (two != null ? false : true);
    }
}