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

import org.meveo.model.catalog.ApplicableEntity;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class SecuredEntityDto.
 *
 * @author anasseh
 */
@XmlRootElement(name = "ApplicableEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicableEntityDto extends BaseEntityDto {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 8941891021770440273L;

    /**
     * The code.
     */
	@Schema(description = "code of the entity applicable")
    @XmlAttribute(required = true)
    private String code;

    /**
     * The entity class.
     */
	@Schema(description = "name of the class applicable")
    @XmlAttribute(required = true)
    private String entityClass;

    /**
     * Instantiates a new secured entity dto.
     */
    public ApplicableEntityDto() {
    }

    /**
     * Instantiates a new secured entity dto.
     *
     * @param entity the entity
     */
    public ApplicableEntityDto(ApplicableEntity entity) {
        this.code = entity.getCode();
        this.entityClass = entity.getEntityClass();
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

}
