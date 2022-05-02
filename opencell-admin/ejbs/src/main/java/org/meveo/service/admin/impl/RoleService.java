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
package org.meveo.service.admin.impl;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.security.client.KeycloakAdminClientService;

/**
 * User Role service implementation.
 */
public class RoleService implements Serializable {

    private static final long serialVersionUID = 6949512629862768876L;

    @Inject
    private KeycloakAdminClientService keycloakAdminClientService;

    /**
     * List/Search the <b>realm</b> roles
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by role name.
     * @return List of roles
     */
    public List<String> list(PaginationConfiguration paginationConfig) {
        return keycloakAdminClientService.listRoles(paginationConfig);
    }

    /**
     * Create a <b>client</b> role as a child of a parent role if provided. An attempt to create a role again will be ignored.
     * 
     * @param name Role name
     * @param parentRole Parent role name. Role will be created if does not exist yet.
     */
    public void createIfAbsent(String name, String parentRole) {
        keycloakAdminClientService.createClientRole(name, parentRole);
    }
}