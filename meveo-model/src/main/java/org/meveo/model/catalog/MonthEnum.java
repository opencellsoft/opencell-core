/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

public enum MonthEnum {
    
    JANUARY(1, "MonthEnum.january"),
    FEBRUARY(2, "MonthEnum.february"),
    MARCH(3, "MonthEnum.march"),
    APRIL(4, "MonthEnum.april"),
    MAY(5, "MonthEnum.may"),
    JUNE(6, "MonthEnum.june"),
    JULY(7, "MonthEnum.july"),
    AUGUST(8, "MonthEnum.august"),
    SEPTEMBER(9, "MonthEnum.september"),
    OCTOBER(10, "MonthEnum.october"),
    NOVEMBER(11, "MonthEnum.november"),
    DECEMBER(12, "MonthEnum.december");

    private Integer id;

    private String label;
    
    MonthEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public static MonthEnum getValue(Integer id) {
        if (id != null) {
            for (MonthEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
    
}
