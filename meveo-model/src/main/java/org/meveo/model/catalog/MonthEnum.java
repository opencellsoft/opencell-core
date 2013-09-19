/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
