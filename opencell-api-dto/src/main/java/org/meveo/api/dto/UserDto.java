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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class UserDto.
 *
 * @author Mohamed Hamidi
 * @since Mai 23, 2016
 * @lastModifiedVersion 5.0.2
 */
@XmlRootElement(name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6633504145323452803L;

    /** The username. */
    @XmlElement(required = true)
    @Schema(description = "the username of the user")
    @NotNull
    private String username;

    /**
     * Used when creating keycloak user.
     */
    @XmlElement()
    @Schema(description = "Used when creating keycloak user")
    private String password;

    /** The email. */
    @XmlElement(required = true)
    @Schema(description = "email of the user")
    @NotNull
    private String email;

    /** The first name. */
    @Schema(description = "first name")
    private String firstName;

    /** The last name. */
    @Schema(description = "last name")
    private String lastName;

    /** The roles. */
    @XmlElementWrapper(name = "userRoles")
    @XmlElement(name = "userRole")
    @Schema(description = "list of role associated to user")
    private List<String> roles;

    /** The external roles. */
    @XmlElementWrapper(name = "externalRoles")
    @XmlElement(name = "externalRole")
    @Schema(description = "list of external role")
    private List<RoleDto> externalRoles;

    /** The secured entities. */
    @XmlElementWrapper(name = "accessibleEntities")
    @XmlElement(name = "accessibleEntity")
    @Schema(description = "list of secured entities associated to the user")
    private List<SecuredEntityDto> securedEntities;

    /** The role. */
    @Deprecated // use roles field
    private String role;

    /** The user level. */
    @XmlElement()
    @Schema(description = "the user level")
    private String userLevel;

    /** The created at. */
    @Schema(description = "date time creation of the user")
    private Date createdAt;

    /** The last login date. */
    @Schema(description = "the last login date")
    private Date lastLoginDate;

    /** The roles. */
    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    @Schema(description = "list of the permission")
    private List<String> permissions;

    /**
     * Instantiates a new user dto.
     */
    public UserDto() {
    }

    /**
     * Instantiates a new user dto.
     *
     * @param user the user
     * @param includeSecuredEntities the include secured entities
     */
    public UserDto(User user, boolean includeSecuredEntities) {
    	super(user);
        if (user.getName() != null) {
            firstName = user.getName().getFirstName();
            lastName = user.getName().getLastName();
        }
        username = user.getUserName();
        email = user.getEmail();
        if (user.getAuditable() != null) {
            createdAt = user.getAuditable().getCreated();
        }
        lastLoginDate = user.getLastLoginDate();

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roles = new ArrayList<String>();
            permissions = new ArrayList<String>();
            for (Role r : user.getRoles()) {
                roles.add(r.getName());
                for (Permission permission : r.getPermissions()) {
                    permissions.add(permission.getPermission());
                }

                role = r.getName();
            }

            Collections.sort(this.roles);
            role = roles.get(roles.size() - 1);
        }

        if (this.permissions != null) {
            Collections.sort(this.permissions);
        }

        if (user.getUserLevel() != null) {
            userLevel = user.getUserLevel().getCode();
        }

        if (includeSecuredEntities && user.getSecuredEntities() != null) {
            this.securedEntities = new ArrayList<>();
            SecuredEntityDto securedEntityDto = null;
            for (SecuredEntity securedEntity : user.getSecuredEntities()) {
                securedEntityDto = new SecuredEntityDto(securedEntity);
                this.securedEntities.add(securedEntityDto);
            }
            Collections.sort(this.securedEntities, Comparator.comparing(SecuredEntityDto::getCode));
        }
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return StringUtils.isBlank(username) ? email : username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles the new roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Gets the secured entities.
     *
     * @return the secured entities
     */
    public List<SecuredEntityDto> getSecuredEntities() {
        return securedEntities;
    }

    /**
     * Sets the secured entities.
     *
     * @param securedEntities the new secured entities
     */
    public void setSecuredEntities(List<SecuredEntityDto> securedEntities) {
        this.securedEntities = securedEntities;
    }

    /**
     * Gets the role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the user level.
     *
     * @return the user level
     */
    public String getUserLevel() {
        return userLevel;
    }

    /**
     * Sets the user level.
     *
     * @param userLevel the new user level
     */
    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the created at.
     *
     * @return the created at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at.
     *
     * @param createdAt the new created at
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last login date.
     *
     * @return the last login date
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the last login date.
     *
     * @param lastLoginDate the new last login date
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Gets the external roles.
     *
     * @return the external roles
     */
    public List<RoleDto> getExternalRoles() {
        return externalRoles;
    }

    /**
     * Sets the external roles.
     *
     * @param externalRoles the new external roles
     */
    public void setExternalRoles(List<RoleDto> externalRoles) {
        this.externalRoles = externalRoles;
    }

    /**
     * @return the permissions
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "UserDto [username=" + username + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + ", roles=" + roles + ", role=" + role + ", userLevel="
                + userLevel + ", securedEntities=" + securedEntities + " ]";
    }
}
