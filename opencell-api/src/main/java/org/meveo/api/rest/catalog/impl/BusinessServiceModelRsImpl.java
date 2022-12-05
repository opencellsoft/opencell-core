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

package org.meveo.api.rest.catalog.impl;

import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.rest.catalog.BusinessServiceModelRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.model.catalog.BusinessServiceModel;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

/**
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2.1
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BusinessServiceModelRsImpl extends BaseRs implements BusinessServiceModelRs {

    @Inject
    private MeveoModuleApi moduleApi;

    @Inject
    private ServiceTemplateApi stApi;

    @Override
    public ActionStatus create(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBusinessServiceModelResponseDto find(String businessServiceModelCode) {
        GetBusinessServiceModelResponseDto result = new GetBusinessServiceModelResponseDto();

        try {
            result.setBusinessServiceModel((BusinessServiceModelDto) moduleApi.find(businessServiceModelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String businessServiceModelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.remove(businessServiceModelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            if (postData != null) {
                if (postData.getServiceTemplate() != null) {
                    if (stApi.find(postData.getServiceTemplate().getCode()) != null ) {
                        moduleApi.createOrUpdate(postData);
                    }
                }
            }
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public MeveoModuleDtosResponse list() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        try {
            result = moduleApi.list(BusinessServiceModel.class);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listGetAll() {

        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();

        try {
            result = moduleApi.list(BusinessServiceModel.class, GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus install(BusinessServiceModelDto moduleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(moduleDto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}