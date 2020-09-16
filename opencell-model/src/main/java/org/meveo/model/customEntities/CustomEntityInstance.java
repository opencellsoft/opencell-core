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

package org.meveo.model.customEntities;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IWFEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;

/**
 * Custom entity instance
 * 
 * @author Andrius Karpavicius
 */
@Entity
@WorkflowedEntity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "CE", cftCodeFields = "cetCode")
@ExportIdentifier({ "code", "cetCode" })
@Table(name = "cust_cei", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "cet_code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cust_cei_seq"), })
public class CustomEntityInstance extends EnableBusinessCFEntity implements IWFEntity {

    private static final long serialVersionUID = 8281478284763353310L;

    /**
     * Custom entity template code
     */
    @Column(name = "cet_code", length = 255, nullable = false)
    @Size(max = 255)
    @NotNull
    private String cetCode;

    /**
     * Parent entity unique identifier UUID. Used only as part of Custom field Embedded entity data type.
     */
    @Column(name = "parent_uuid", updatable = false, length = 60)
    @Size(max = 60)
    private String parentEntityUuid;

    public String getCetCode() {
        return cetCode;
    }

    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    public void setParentEntityUuid(String parentEntityUuid) {
        this.parentEntityUuid = parentEntityUuid;
    }

    public String getParentEntityUuid() {
        return parentEntityUuid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cetCode == null) ? 0 : cetCode.hashCode());
        result = prime * result + ((parentEntityUuid == null) ? 0 : parentEntityUuid.hashCode());
        return result;
    }

    /**
     * @see org.meveo.model.BusinessEntity#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomEntityInstance)) {
            return false;
        }

        CustomEntityInstance other = (CustomEntityInstance) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (code == null && other.getCode() != null) {
            return false;
        } else if (code != null && !code.equals(other.getCode())) {
            return false;
        } else if (cetCode == null && other.getCetCode() != null) {
            return false;
        } else if (cetCode != null && !cetCode.equals(other.getCetCode())) {
            return false;
        }
        return true;
    }

}