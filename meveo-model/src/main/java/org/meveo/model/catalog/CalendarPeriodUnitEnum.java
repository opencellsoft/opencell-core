package org.meveo.model.catalog;

/**
 * Defines period measurement unit
 * 
 * @author Andrius Karpavicius
 * 
 */
public enum CalendarPeriodUnitEnum {

    /**
     * A period measured in months
     */
    MONTH(java.util.Calendar.MONTH),

    /**
     * A period measured in days
     */
    DAY_OF_MONTH(java.util.Calendar.DAY_OF_MONTH),

    /**
     * A period measured in hours
     */
    HOUR_OF_DAY(java.util.Calendar.HOUR_OF_DAY),

    /**
     * A period measured in minutes
     */
    MINUTE(java.util.Calendar.MINUTE),

    /**
     * A period measured in seconds
     */
    SECOND(java.util.Calendar.SECOND);

    /*
     * Corresponding java.util.Calendar constant
     */
    private int unitValue;

    private CalendarPeriodUnitEnum(int unitValue) {
        this.unitValue = unitValue;
    }

    public int getUnitValue() {
        return unitValue;
    }

    /**
     * Find a corresponding CalendarPeriodUnitEnum by its unit value
     * 
     * @param unit Unit value to match
     * @return Matched CalendarPeriodUnitEnum
     */
    public static CalendarPeriodUnitEnum getValueByUnit(int unit) {
        for (CalendarPeriodUnitEnum enumValue : CalendarPeriodUnitEnum.values()) {
            if (enumValue.getUnitValue() == unit) {
                return enumValue;
            }
        }

        return null;
    }
}