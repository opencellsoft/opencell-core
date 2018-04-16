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
    UNION;

    /**
     * Checks if is join.
     *
     * @return true, if is join
     */
    public boolean isJoin() {
        return this == INTERSECT || this == UNION;
    }
}