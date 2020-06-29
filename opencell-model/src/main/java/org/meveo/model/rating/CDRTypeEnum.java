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

package org.meveo.model.rating;

/**
 * 
 * @author h.znibar
 *
 */
public enum CDRTypeEnum {

	 CSV(1, "cdrType.csv"), JSON(2, "cdrType.json");

    private Integer id;
    private String label;

    private CDRTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static CDRTypeEnum getValue(Integer id) {
        if (id != null) {
            for (CDRTypeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
    public static CDRTypeEnum getByLabel(String label) {
        if (label != null) {
            for (CDRTypeEnum type : values()) {
                if (label.equals(type.getLabel())) {
                    return type;
                }
            }
        }
        return null;
    }
    public String toString() {
        return name();
    }

}
