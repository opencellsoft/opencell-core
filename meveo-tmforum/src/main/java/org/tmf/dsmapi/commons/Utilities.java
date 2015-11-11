package org.tmf.dsmapi.commons;

import java.util.List;

/**
 *
 * @author baman.barzideh
 *
 */
public class Utilities {

    private Utilities() {
    }

    public static boolean areEqual(Object objectOne, Object objectTwo) {
        if (objectOne == null) {
            return (objectTwo == null) ? true : false;
        }

        return objectOne.equals(objectTwo);
    }

    public static boolean hasValue(String input) {
        if (input == null) {
            return false;
        }

        if (input.trim().length() <= 0) {
            return false;
        }

        return true;
    }

    public static boolean hasContents(List<?> input) {
        if (input == null || input.size() <= 0) {
            return false;
        }

        return true;
    }

}
