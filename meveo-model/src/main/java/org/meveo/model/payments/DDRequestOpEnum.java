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
/**
 * 
 */
package org.meveo.model.payments;

public enum DDRequestOpEnum {

    CREATE(1, "DDRequestOpEnum.CREATE"),
    PAYMENT(2, "DDRequestOpEnum.PAYMENT"),
    FILE(3, "DDRequestOpEnum.FILE");

    private Integer id;
    private String label;

    DDRequestOpEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static DDRequestOpEnum getValue(Integer id) {
        if (id != null) {
            for (DDRequestOpEnum value : values()) {
                if (value.getId() == id) {
                    return value;
                }
            }
        }
        return null;
    }

}
