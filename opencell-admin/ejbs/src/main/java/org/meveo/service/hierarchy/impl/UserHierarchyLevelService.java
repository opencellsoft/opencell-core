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
package org.meveo.service.hierarchy.impl;

import java.io.Serializable;
import java.util.List;

import jakarta.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.security.UserGroup;
import org.meveo.security.client.KeycloakAdminClientService;

/**
 * User Hierarchy Level service implementation.
 */
public class UserHierarchyLevelService implements Serializable {

    private static final long serialVersionUID = -7192305172262297151L;

    @Inject
    private KeycloakAdminClientService keycloakAdminClientService;

    /**
     * List/Search the user groups
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by group name.
     * @return List of user groups
     */
    public List<UserGroup> list(PaginationConfiguration paginationConfig) {
        return keycloakAdminClientService.listGroups(paginationConfig);
    }

    /**
     * Find a user group by a name in Keycloak
     * 
     * @param userGroupName User group name to match
     * @return A user group including it's children
     */
    public UserGroup findByCode(String code) {
        return keycloakAdminClientService.findGroup(code);
    }
}