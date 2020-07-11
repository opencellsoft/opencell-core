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

public enum CDRStatusEnum {

	 OPEN(1, "cdrStatus.open"), PROCESSED(2, "cdrStatus.processed"), CLOSED(3, "cdrStatus.closed"), DISCARDED(4, "cdrStatus.discarded"), ERROR(5, "cdrStatus.error"),TO_REPROCESS(6, "cdrStatus.toReprocess");

    private Integer id;
    private String label;

    private CDRStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static CDRStatusEnum getValue(Integer id) {
        if (id != null) {
            for (CDRStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
    public static CDRStatusEnum getByLabel(String label) {
        if (label != null) {
            for (CDRStatusEnum status : values()) {
                if (label.equals(status.getLabel())) {
                    return status;
                }
            }
        }
        return null;
    }
    public String toString() {
        return name();
    }

}
