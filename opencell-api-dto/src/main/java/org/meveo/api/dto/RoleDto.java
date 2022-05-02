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

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class RoleDto.
 * 
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@XmlRootElement(name = "Role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    @XmlAttribute(required = true)
    @Schema(description = "the name of the role")
    @NotNull
    private String name;

    /** The description. */
    @XmlAttribute
    @Schema(description = "short description of the role")
    private String description;

    /** The secured entities. */
    @XmlElementWrapper(name = "accessibleEntities")
    @XmlElement(name = "accessibleEntity")
    @Schema(description = "list of the secured entities")
    private List<SecuredEntityDto> securedEntities;

    /**
     * Instantiates a new role dto.
     */
    public RoleDto() {

    }
    /**
     * Instantiates a new role dto.
     *
     * @param role The role name
     */
    public RoleDto(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("RoleDto [name=%s]", name);
    }

    /**
     * Returns a list of secured entities
     */
    public List<SecuredEntityDto> getSecuredEntities() {
        return securedEntities;
    }

    /**
     * Gets a list of secured entities
     */
    public void setSecuredEntities(List<SecuredEntityDto> securedEntities) {
        this.securedEntities = securedEntities;
    }
}