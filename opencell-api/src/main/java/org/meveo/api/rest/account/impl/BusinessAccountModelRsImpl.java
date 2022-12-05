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

package org.meveo.api.rest.account.impl;

import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CRMAccountTypeSearchDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.response.ParentListResponse;
import org.meveo.api.dto.response.account.BusinessAccountModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.rest.account.BusinessAccountModelRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.model.crm.BusinessAccountModel;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BusinessAccountModelRsImpl extends BaseRs implements BusinessAccountModelRs {

    @Inject
    private MeveoModuleApi moduleApi;

    @Inject
    AccountHierarchyApi accountHierarchyApi;

    @Override
    public ActionStatus create(BusinessAccountModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(BusinessAccountModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public BusinessAccountModelResponseDto find(String bamCode) {
        BusinessAccountModelResponseDto result = new BusinessAccountModelResponseDto();

        try {
            result.setBusinessAccountModel((BusinessAccountModelDto) moduleApi.find(bamCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String bamCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.remove(bamCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse list() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        try {
            result = moduleApi.list(BusinessAccountModel.class);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listGetAll() {

        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();

        try {
            result = moduleApi.list(BusinessAccountModel.class, GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus install(BusinessAccountModelDto moduleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(moduleDto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ParentListResponse findParents(CRMAccountTypeSearchDto searchDto) {
        ParentListResponse result = new ParentListResponse();

        try {
            result.setParents(accountHierarchyApi.getParentList(searchDto));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}