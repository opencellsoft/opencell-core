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

package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.RoleApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.RolesDto;
import org.meveo.api.dto.response.GetRoleResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.RoleRs;

/**
 * REST API for managing {@link Role}.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RoleRsImpl extends BaseRs implements RoleRs {

    @Inject
    private RoleApi roleApi;

    @Override
    public ActionStatus create(RoleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(RoleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String roleName) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.remove(roleName);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetRoleResponse find(String roleName, boolean includeSecuredEntities) {
        GetRoleResponse result = new GetRoleResponse();
        try {
            result.setRoleDto(roleApi.find(roleName, includeSecuredEntities));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetRoleResponse findV2(String roleName, boolean includeSecuredEntities) {
        return find(roleName, includeSecuredEntities);
    }

    @Override
    public ActionStatus createOrUpdate(RoleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public RolesDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        RolesDto result = new RolesDto();

        try {
            result = roleApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public RolesDto listGetV2(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        return listGet(query, fields, offset, limit, sortBy, sortOrder);
    }

    @Override
    public RolesDto listPost(PagingAndFiltering pagingAndFiltering) {

        RolesDto result = new RolesDto();

        try {
            result = roleApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public RolesDto listPostV2(PagingAndFiltering pagingAndFiltering) {
        return listPost(pagingAndFiltering);
    }

    @Override
    public RolesDto listExternalRoles() {
        RolesDto result = new RolesDto();

        try {
            result.setRoles(roleApi.listExternalRoles(httpServletRequest));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public RolesDto listExternalRolesV2() {
        return listExternalRoles();
    }
}