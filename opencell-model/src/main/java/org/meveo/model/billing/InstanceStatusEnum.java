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

public enum InstanceStatusEnum {

    ACTIVE(1, "accountStatusEnum.active"), INACTIVE(1, "accountStatusEnum.inactive"), CANCELED(2, "accountStatusEnum.canceled"), TERMINATED(3, "accountStatusEnum.terminated"), SUSPENDED(4,
            "accountStatusEnum.suspended"), CLOSED(4, "accountStatusEnum.closed");

    private Integer id;

    private String label;

    InstanceStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;

    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets enum by its id.
     * 
     * @param id id of instance status
     * @return instance status.
     */
    public static InstanceStatusEnum getValue(Integer id) {
        if (id != null) {
            for (InstanceStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }

    /**
     * Is this a final status
     * 
     * @return True for Canceled, Closed and Terminated statuses
     */
    public boolean isFinalStatus() {
        return this == CANCELED || this == CLOSED || this == TERMINATED;
    }
}
