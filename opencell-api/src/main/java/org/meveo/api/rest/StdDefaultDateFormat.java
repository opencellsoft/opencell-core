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

package org.meveo.api.rest;

import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class StdDefaultDateFormat extends StdDateFormat {
    
    private static final long serialVersionUID = 1L;

    static {
        Calendar defCal = Calendar.getInstance(TimeZone.getDefault());
        
        DATE_FORMAT_RFC1123.setCalendar(defCal);
        DATE_FORMAT_ISO8601.setCalendar(defCal);
        // not found in jackson 2.9.9
//        DATE_FORMAT_ISO8601_Z.setCalendar(defCal);
//        DATE_FORMAT_PLAIN.setCalendar(defCal);
    }

}
