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

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;
import org.meveo.security.MeveoUser;

/**
 * The Class UserDto.
 *
 * @author Mohamed Hamidi
 * @since Mai 23, 2016
 * @lastModifiedVersion 5.0.2
 */
@XmlRootElement(name = "CurrentUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrentUserDto extends BaseEntityDto {

    private static final long serialVersionUID = 316622689251149958L;

    /** The username. */
    @XmlElement(required = true)
    private String username;

    /** The email. */
    @XmlElement(required = true)
    private String email;

    /** The full name. */
    private String fullName;

    /** The roles. */
    @XmlElementWrapper(name = "rolesByApplication")
    @XmlElement(name = "role")
    private Map<String, Set<String>> rolesByApplication;

    /**
     * Instantiates a new dto
     */
    public CurrentUserDto() {
    }

    /**
     * Instantiates a new dto.
     *
     * @param user The current user
     * @param rolesByApplication Roles grouped by application (keycloak client)
     */
    public CurrentUserDto(MeveoUser user, Map<String, Set<String>> rolesByApplication) {
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.username = user.getUserName();
        this.rolesByApplication = rolesByApplication;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Get roles grouped by application (keycloak client)
     * 
     * @return A list of roles grouped by application (keycloak client). A realm level roles are identified by key "realm". Admin application (KC client opencell-web) contains
     *         a mix or realm roles, client roles, roles defined in opencell and their resolution to permissions.
     */
    public Map<String, Set<String>> getRolesByApplication() {
        return rolesByApplication;
    }
}