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

package org.meveo.api.rest.hierarchy.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.UserHierarchyLevelResponseDto;
import org.meveo.api.hierarchy.UserHierarchyLevelApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.hierarchy.UserHierarchyLevelRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Phu Bach
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UserHierarchyLevelRsImpl extends BaseRs implements UserHierarchyLevelRs {

    @Inject
    private UserHierarchyLevelApi userHierarchyLevelApi;

    @Override
    public ActionStatus create(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        return result;
    }

    @Override
    public ActionStatus update(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");


        return result;
    }

    @Override
    public UserHierarchyLevelResponseDto find(String hierarchyLevelCode) {
        UserHierarchyLevelResponseDto result = new UserHierarchyLevelResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setUserHierarchyLevel(userHierarchyLevelApi.find(hierarchyLevelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String hierarchyLevelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");


        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UserHierarchyLevelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        
        return result;
    }

    @Override
    public UserHierarchyLevelsDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        UserHierarchyLevelsDto result = new UserHierarchyLevelsDto();

        try {
            result = userHierarchyLevelApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UserHierarchyLevelsDto listPost(PagingAndFiltering pagingAndFiltering) {

        UserHierarchyLevelsDto result = new UserHierarchyLevelsDto();

        try {
            result = userHierarchyLevelApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}