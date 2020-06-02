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
package org.meveo.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a business entity that can be uniquely identified by a code
 * 
 * @author Andrius Karpavicius
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@ReferenceIdentifierCode("code")
@ReferenceIdentifierDescription("description")
@MappedSuperclass
public abstract class BusinessEntity extends AuditableEntity implements ISearchable, IReferenceEntity {

    private static final long serialVersionUID = 1L;

    // TODO : Create sql script to add index. @Index(name = "CODE_IDX")
    /**
     * Code
     */
    @Column(name = "code", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    @JsonProperty
    protected String code;

    /**
     * Used to track if "Code" field value has changed. Value is populated on postLoad, postPersist and postUpdate JPA events
     */
    @Transient
    protected String previousCode;

    /**
     * Description
     */
    @Column(name = "description", nullable = true, length = 255)
    @Size(max = 255)
    @JsonProperty
    protected String description;

    /**
     * If True, append Identifier to the code value
     */
    @Transient
    protected boolean appendGeneratedCode = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code : "" ;
    }

    public String getDescription() {
        if(description == null) {
            return "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the appendGeneratedCode
     */
    public boolean isAppendGeneratedCode() {
        return appendGeneratedCode;
    }

    /**
     * @param appendGeneratedCode the appendGeneratedCode to set
     */
    public void setAppendGeneratedCode(boolean appendGeneratedCode) {
        this.appendGeneratedCode = appendGeneratedCode;
    }

    /**
     * @return Description or code value if description was not provided
     */
    public String getDescriptionOrCode() {
        if (!StringUtils.isBlank(description)) {
            return description;
        } else {
            return code;
        }
    }

    /**
     * @return Description with code in the following format: &lt;code&gt;_&lt;description&gt;
     */
    public String getDescriptionAndCode() {
        if (!StringUtils.isBlank(description)) {
            return code + " - " + description;
        } else {
            return code;
        }
    }

    /**
     * This method can be overridden to allow child entities to identify their parent entity.
     * 
     * @return The parent entity.
     */
    public BusinessEntity getParentEntity() {
        return null;
    }

    /**
     * This method can be overridden to allow child entities to identify their parent entity's type.
     * 
     * @return The parent entity's type.
     */
    public Class<? extends BusinessEntity> getParentEntityType() {
        if (getParentEntity() != null) {
            return getParentEntity().getClass();
        }
        return null;
    }

    @Override
    public int hashCode() {
        return 961 + (this.getClass().getName() + code).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof BusinessEntity)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        BusinessEntity other = (BusinessEntity) obj;

        if (id != null && other.getId() != null && getClass().equals(other.getClass()) && id.equals(other.getId())) {
            return true;
        }

        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s[%s, code=%s]", this.getClass().getSimpleName(), super.toString(), code);
    }

    /**
     * A "fake" method needed for JSF functionality as a setter equivalent for getDescriptionOrCode()
     * 
     * @param description Description
     */
    public void setDescriptionOrCode(String description) {
        setDescription(description);
    }

    /**
     * Check if current and previous "Code" field values match. Note: previous value is set to current value at postLoad, postPersist, postUpdate JPA events
     * 
     * @return True if current and previous "Code" field values DO NOT match
     */
    public boolean isCodeChanged() {
        return !StringUtils.equals(code, previousCode);
    }

    @Override
    public String getReferenceCode() {
        return code;
    }

    @Override
    public void setReferenceCode(Object value) {
        setCode(value.toString());
    }

    @Override
    public String getReferenceDescription() {
        return description;
    }
    
}