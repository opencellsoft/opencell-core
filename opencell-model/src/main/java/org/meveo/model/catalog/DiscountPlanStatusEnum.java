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
 * @author Khalid HORRI
 **/
public enum DiscountPlanStatusEnum {
    ACTIVE(1, "DiscountPlanStatusEnum.active"), INACTIVE(2, "DiscountPlanStatusEnum.inactive"), DRAFT(3, "DiscountPlanStatusEnum.draft"), IN_USE(4,
            "DiscountPlanStatusEnum.inUse"), EXPIRED(5, "DiscountPlanStatusEnum.expired");
    /**
     * Enum Id
     */
    private Integer id;
    /**
     * Enum label.
     */
    private String label;

    /**
     * @param id    Enum Id
     * @param label enum label
     */
    DiscountPlanStatusEnum(final Integer id, final String label) {
        this.id = id;
        this.label = label;
    }

    /**
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     * @return
     */
    public static DiscountPlanStatusEnum getValue(Integer id) {
        if (id != null) {
            for (DiscountPlanStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
