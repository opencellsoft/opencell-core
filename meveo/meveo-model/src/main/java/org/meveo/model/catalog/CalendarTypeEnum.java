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

public enum CalendarTypeEnum {

    CHARGE_IMPUTATION(1, "calendarTypeEnum.chargeImputation"),
    BILLING(2, "calendarTypeEnum.billing"),
    DURATION_TERM(3, "calendarTypeEnum.durationTerm"),
    COUNTER(4, "calendarTypeEnum.counter");

    private Integer id;
    private String label;

    CalendarTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public static CalendarTypeEnum getValue(Integer id) {
        if (id != null) {
            for (CalendarTypeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }

}
