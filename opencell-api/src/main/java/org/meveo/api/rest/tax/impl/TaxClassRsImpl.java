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

package org.meveo.api.rest.tax.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxClassListResponseDto;
import org.meveo.api.dto.response.tax.TaxClassResponseDto;
import org.meveo.api.dto.tax.TaxClassDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tax.TaxClassRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.api.tax.TaxClassApi;
import org.meveo.commons.utils.ExceptionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

/**
 * REST interface definition of Tax class API
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxClassRsImpl extends BaseRs implements TaxClassRs {

    @Inject
    private TaxClassApi apiService;

    @Override
    public ActionStatus create(TaxClassDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.create(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxClassResponseDto find(String code) {
        TaxClassResponseDto result = new TaxClassResponseDto();

        try {
            result.setDto(apiService.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(TaxClassDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.update(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.remove(code);
        } catch (Exception e) {
            processException(beautifyForeignConstraintViolationMessage(e), result);
        }

        return result;
    }

    private Exception beautifyForeignConstraintViolationMessage(Exception e) {
        if(ExceptionUtils.getRootCause(e).getMessage().contains("violates foreign key constraint"))
        {

            return new Exception("you cannot delete tax class. it still referenced by other entities ");
        }

        return e;
    }

    @Override
    public ActionStatus createOrUpdate(TaxClassDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxClassListResponseDto searchGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        TaxClassListResponseDto result;

        try {
            result = new TaxClassListResponseDto(apiService.search(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder)));
        } catch (Exception e) {
            result = new TaxClassListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TaxClassListResponseDto listGetAll() {

        TaxClassListResponseDto result = new TaxClassListResponseDto();

        try {
            result = apiService.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TaxClassListResponseDto searchPost(PagingAndFiltering pagingAndFiltering) {

        TaxClassListResponseDto result;

        try {
            result = new TaxClassListResponseDto(apiService.search(pagingAndFiltering));
        } catch (Exception e) {
            result = new TaxClassListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }
}