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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.meveo.api.UserApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.response.GetCurrentUserResponse;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.UserRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;

/**
 * @author Mohamed Hamidi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UserRsImpl extends BaseRs implements UserRs {

    @Inject
    private UserApi userApi;

    @Override
    public ActionStatus create(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(@PathParam("username") String username) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.remove(username);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUserResponse find(@QueryParam("username") String username) {
        GetUserResponse result = new GetUserResponse();

        try {
            result.setUser(userApi.find(username));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetUserResponse findV2(String username) {
        return find(username);
    }

    @Override
    public ActionStatus createOrUpdate(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    public UsersDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        UsersDto result = new UsersDto();

        try {
            result = userApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UsersDto listGetV2(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        return listGet(query, fields, offset, limit, sortBy, sortOrder);
    }

    public UsersDto list() {

        UsersDto result = new UsersDto();

        try {
            result = userApi.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UsersDto listPost(PagingAndFiltering pagingAndFiltering) {

        UsersDto result = new UsersDto();

        try {
            result = userApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UsersDto listPostV2(PagingAndFiltering pagingAndFiltering) {
        return listPost(pagingAndFiltering);
    }

    @Override
    public ActionStatus createExternalUser(UserDto postData) {
        return create(postData);
    }

    @Override
    public ActionStatus updateExternalUser(UserDto postData) {
        return update(postData);
    }

    @Override
    public ActionStatus deleteExternalUser(String username) {
        return remove(username);
    }

    @Override
    public GetCurrentUserResponse getCurrentUser() {
        return new GetCurrentUserResponse(userApi.getCurrentUser());
    }
}