/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.model.payments;

public enum DunningLevelEnum {

    R0(1, "DunningLevelEnum.R0"),
    R1(2, "DunningLevelEnum.R1"),
    R2(3, "DunningLevelEnum.R2"),
    R3(4, "DunningLevelEnum.R3");

    private Integer id;
    private String label;

    DunningLevelEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static DunningLevelEnum getValue(Integer id) {
        if (id != null) {
            for (DunningLevelEnum status : values()) {
                if (status.getId().intValue() == id.intValue()) {
                    return status;
                }
            }
        }
        return null;
    }
}
