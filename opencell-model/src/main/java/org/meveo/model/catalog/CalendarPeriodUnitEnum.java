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