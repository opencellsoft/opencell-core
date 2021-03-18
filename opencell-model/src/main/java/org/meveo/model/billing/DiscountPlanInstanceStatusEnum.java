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

package org.meveo.model.billing;

import org.meveo.model.catalog.DiscountPlanStatusEnum;

/**
 * @author Khalid HORRI
 **/
public enum DiscountPlanInstanceStatusEnum {
    ACTIVE(1, "DiscountPlanStatusEnum.active"), APPLIED(2, "DiscountPlanStatusEnum.draft"), IN_USE(3, "DiscountPlanStatusEnum.inUse"), EXPIRED(4, "DiscountPlanStatusEnum.expired");
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
    DiscountPlanInstanceStatusEnum(final Integer id, final String label) {
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
    public static DiscountPlanInstanceStatusEnum getValue(Integer id) {
        if (id != null) {
            for (DiscountPlanInstanceStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
