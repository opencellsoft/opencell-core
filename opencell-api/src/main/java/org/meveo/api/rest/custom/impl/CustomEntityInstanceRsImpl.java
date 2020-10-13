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

package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CustomEntityInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.CustomEntityInstanceRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * Rest API implementation for custom entity instance management
 * 
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomEntityInstanceRsImpl extends BaseRs implements CustomEntityInstanceRs {

    @Inject
    private CustomEntityInstanceApi customEntityInstanceApi;

    @Override
    public ActionStatus create(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.create(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.update(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String customEntityTemplateCode, String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            customEntityInstanceApi.remove(customEntityTemplateCode, code);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomEntityInstanceResponseDto find(String customEntityTemplateCode, String code) {
        CustomEntityInstanceResponseDto result = new CustomEntityInstanceResponseDto();

        try {

            result.setCustomEntityInstance(customEntityInstanceApi.find(customEntityTemplateCode, code));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public CustomEntityInstancesResponseDto list(String customEntityTemplateCode) {
        return list(customEntityTemplateCode, null);
    }

    @Override
    public CustomEntityInstancesResponseDto list(String customEntityTemplateCode, PagingAndFiltering pagingAndFiltering) {

        try {

            return customEntityInstanceApi.list(customEntityTemplateCode, pagingAndFiltering);

        } catch (Exception e) {
            CustomEntityInstancesResponseDto result = new CustomEntityInstancesResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public ActionStatus createOrUpdate(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.createOrUpdate(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String customEntityTemplateCode, String code) {
        ActionStatus result = new ActionStatus();

        try {
            customEntityInstanceApi.enableOrDisable(customEntityTemplateCode, code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String customEntityTemplateCode, String code) {
        ActionStatus result = new ActionStatus();

        try {
            customEntityInstanceApi.enableOrDisable(customEntityTemplateCode, code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}