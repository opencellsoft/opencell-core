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

import org.meveo.api.catalog.UnitOfMeasureApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.catalog.GetListUnitOfMeasureResponseDto;
import org.meveo.api.dto.response.catalog.GetUnitOfMeasureResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.UnitOfMeasureRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
/**
 * @author Mounir Bahije
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UnitOfMeasureRsImpl extends BaseRs implements UnitOfMeasureRs {

    @Inject
    private UnitOfMeasureApi unitOfMeasureApi;

    @Override
    public ActionStatus create(UnitOfMeasureDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(UnitOfMeasureDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUnitOfMeasureResponseDto find(String unitOfMeasureCode) {
        GetUnitOfMeasureResponseDto result = new GetUnitOfMeasureResponseDto();

        try {
            result.setUnitOfMeasure(unitOfMeasureApi.find(unitOfMeasureCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus delete(String unitOfMeasureCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.remove(unitOfMeasureCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UnitOfMeasureDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetListUnitOfMeasureResponseDto list() {
        GetListUnitOfMeasureResponseDto result = new GetListUnitOfMeasureResponseDto();

        try {
            result.setListUnitOfMeasure(unitOfMeasureApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetListUnitOfMeasureResponseDto listGetAll() {

        GetListUnitOfMeasureResponseDto result = new GetListUnitOfMeasureResponseDto();

        try {
            result = unitOfMeasureApi.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}