/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

public enum InstanceStatusEnum {

    ACTIVE(1, "accountStatusEnum.active"), INACTIVE(1, "accountStatusEnum.inactive"), CANCELED(2, "accountStatusEnum.canceled"), TERMINATED(3,
            "accountStatusEnum.terminated"), SUSPENDED(4, "accountStatusEnum.suspended"), CLOSED(4, "accountStatusEnum.closed");

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
}
