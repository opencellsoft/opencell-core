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
