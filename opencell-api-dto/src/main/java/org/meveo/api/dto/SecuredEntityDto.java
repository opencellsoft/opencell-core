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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.SecuredEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class SecuredEntityDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "SecuredEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecuredEntityDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8941891021770440273L;

    /** The code. */
    @XmlAttribute(required = true)
    @Schema(description = "The code", required = true)
    private String code;

    /** The entity class. */
    @XmlAttribute(required = true)
    @Schema(description = "The entity class", required = true)
    private String entityClass;

    @XmlAttribute(required = true)
    @Schema(description = "indicate of the entity is disabled", required = true, defaultValue = "false")
    private boolean disabled;

    /**
     * Instantiates a new secured entity dto.
     */
    public SecuredEntityDto() {
    }

    /**
     * Instantiates a new secured entity dto.
     *
     * @param entity the entity
     */
    public SecuredEntityDto(SecuredEntity entity) {
        this.code = entity.getCode();
        this.entityClass = entity.getEntityClass();
        this.disabled = entity.isDisabled();
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
     * Gets the entity class.
     *
     * @return the entity class
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * Sets the entity class.
     *
     * @param entityClass the new entity class
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
