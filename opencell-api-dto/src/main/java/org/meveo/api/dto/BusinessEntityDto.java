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

package org.meveo.api.dto;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.BusinessEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Equivalent of BusinessEntity in DTO
 * 
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 * @lastModifiedVersion 5.0.1
 **/
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessEntityDto extends AuditableEntityDto implements IEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4451119256601996946L;

    /** The id. */
    @XmlAttribute()
    protected Long id;

    /**
     * The code
     */
    // @Pattern(regexp = "^[@A-Za-z0-9_\\.-]+$")
    @XmlAttribute(required = true)
    @NotNull
    @Schema(description = "The code of the entity")
    protected String code;

    /**
     * The description
     */
    @XmlAttribute()
    @Schema(description = "The description of the entity")
    protected String description;

    /** The updated code. */
    @Schema(description = "The changed code")
    protected String updatedCode;

    /**
     * Instantiates a new business dto.
     */
    public BusinessEntityDto() {

    }

    /**
     * Converts BusinessEntity JPA entity to DTO
     *
     * @param e Entity to convert
     */
    public BusinessEntityDto(BusinessEntity e) {
        super(e);
        if (e != null) {
            id = e.getId();
            code = e.getCode();
            description = e.getDescription();
        }
    }

    /**
     * Gets the updated code.
     *
     * @return the updated code
     */
    public String getUpdatedCode() {
        return updatedCode;
    }

    /**
     * Sets the updated code.
     *
     * @param updatedCode the new updated code
     */
    public void setUpdatedCode(String updatedCode) {
        this.updatedCode = updatedCode;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
