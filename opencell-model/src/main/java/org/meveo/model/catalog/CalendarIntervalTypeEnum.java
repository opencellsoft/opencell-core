package org.meveo.model.catalog;

/**
 * Defines interval measurement unit
 * 
 * @author Andrius Karpavicius
 * 
 */
public enum CalendarIntervalTypeEnum {

    /**
     * A range of month/day E.g. 01/01-02/01, 02/01-03/01
     */
    DAY,

    /**
     * A range of time E.g. 08:00-14:00, 15:00-17:00
     */
    HOUR,

    /**
     * A range of weekdays E.g. Monday-Friday
     */
    WDAY;

    public String getLabel() {
        return "CalendarIntervalTypeEnum." + this.name();
    }
}
