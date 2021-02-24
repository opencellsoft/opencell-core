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

package org.meveo.api.dto;


/**
 * The Enum CalendarTypeEnum.
 */
public enum CalendarTypeEnum {

    /**
     * Month day based calendar. E.g. 01/01, 02/01, 03/01, etc. would result in month long intervals each year
     */
    YEARLY,

    /**
     * Time based calendar. E.g. 12:00, 24:00 would result in 12 hour intervals each day.
     */
    DAILY,

    /**
     * A period of X months, days, hours, minutes, seconds. 
     * See CalendarPeriodUnitEnum for unit definition.
     */
    PERIOD,

    /**
     * A range of time, month/day or weekdays 
     * E.g. 08:00-14:00, 15:00-17:00 or 01/01-02/01, 02/01-03/01 or Monday-Friday
     */
    INTERVAL,

    /**
     * An intersection of two calendars. An intersection of "Monday-Monday" and "Saturday-Monday" would result in "Monday-Saturday" time range.
     */
    INTERSECT,

    /**
     * A union of two calendars. A union of calendars "Monday-Tuesday" and "Tuesday-Friday" would result in "Monday-Friday" time range
     */
    UNION,
    
    /**
     * An append of two calendars. The second calendar is only used after consuming the main calendar
     */
    APPEND,
    
    /**
     * A union of two calendars. A union of calendars "Monday-Tuesday" and "Tuesday-Friday" would result in "Monday-Friday" time range
     */
    BANKING,
    
    /**
     * Fixed Date calendar. E.g. 12/02/2017 11:13:12.
     */
    FIXED;
    
    /**

    /**
     * Checks if is join.
     *
     * @return true, if is join
     */
    public boolean isJoin() {
        return this == INTERSECT || this == UNION;
    }
}