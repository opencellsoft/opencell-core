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

public enum CounterTypeEnum {

    USAGE(1, "counterTypeEnum.usage", false), NOTIFICATION(2, "counterTypeEnum.notification", false), USAGE_AMOUNT(3, "counterTypeEnum.usageAmount", true);
    private Integer id;
    private String label;
    private boolean isAccumulator;

    /**
     * @param id            the ID
     * @param label         the label
     * @param isAccumulator true if is it an accumulator counter type
     */
    CounterTypeEnum(Integer id, String label, boolean isAccumulator) {
        this.id = id;
        this.label = label;
        this.isAccumulator = isAccumulator;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isAccumulator() {
        return isAccumulator;
    }

    public static CounterTypeEnum getValue(Integer id) {
        if (id != null) {
            for (CounterTypeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
