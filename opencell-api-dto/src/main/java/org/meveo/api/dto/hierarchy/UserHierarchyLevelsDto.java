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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class UserHierarchyLevelsDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "UserHierarchyLevels")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelsDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8948684323709076291L;

    /** The user hierarchy levels. */
    @XmlElementWrapper(name = "userHierarchyLevels")
    @XmlElement(name = "userHierarchyLevel")
    private List<UserHierarchyLevelDto> userHierarchyLevels = new ArrayList<>();

    /**
     * Gets the user hierarchy levels.
     *
     * @return the user hierarchy levels
     */
    public List<UserHierarchyLevelDto> getUserHierarchyLevels() {
        return userHierarchyLevels;
    }

    /**
     * Sets the user hierarchy levels.
     *
     * @param userHierarchyLevels the new user hierarchy levels
     */
    public void setUserHierarchyLevels(List<UserHierarchyLevelDto> userHierarchyLevels) {
        this.userHierarchyLevels = userHierarchyLevels;
    }
}