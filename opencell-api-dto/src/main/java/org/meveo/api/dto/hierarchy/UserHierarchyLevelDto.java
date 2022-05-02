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

package org.meveo.api.dto.hierarchy;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.security.UserGroup;

/**
 * The Class UserHierarchyLevelDto.
 *
 * @author Phu Bach
 */
@XmlRootElement(name = "UserHierarchyLevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1332916104721562522L;

    /** The parent level. */
    private String parentLevel;

    /** The child levels. */
    @XmlElementWrapper(name = "childLevels")
    @XmlElement(name = "userHierarchyLevel")
    private List<UserHierarchyLevelDto> childLevels;

    /**
     * Instantiates a new user hierarchy level dto.
     */
    public UserHierarchyLevelDto() {

    }

    /**
     * Instantiates a new user hierarchy level dto.
     *
     * @param level the HierarchyLevel
     */
    public UserHierarchyLevelDto(UserGroup level) {
        parentLevel = level.getParentGroup();
        code = level.getName();
    }

    /**
     * Gets the parent level.
     *
     * @return the parent level
     */
    public String getParentLevel() {
        return parentLevel;
    }

    /**
     * Sets the parent level.
     *
     * @param parentLevel the new parent level
     */
    public void setParentLevel(String parentLevel) {
        this.parentLevel = parentLevel;
    }

    /**
     * Gets the child levels.
     *
     * @return the child levels
     */
    public List<UserHierarchyLevelDto> getChildLevels() {
        return childLevels;
    }

    /**
     * Sets the child levels.
     *
     * @param childLevels the new child levels
     */
    public void setChildLevels(List<UserHierarchyLevelDto> childLevels) {
        this.childLevels = childLevels;
    }
}