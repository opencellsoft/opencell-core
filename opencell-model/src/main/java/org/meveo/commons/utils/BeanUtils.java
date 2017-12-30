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
}