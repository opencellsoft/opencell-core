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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.custom.CustomTableApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.custom.CustomTableWrapperDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.CustomTableRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * Rest API implementation for custom table data management
 * 
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomTableRsImpl extends BaseRs implements CustomTableRs {

    @Inject
    private CustomTableApi customTableApi;

    @Override
    public ActionStatus append(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus();

        try {

            customTableApi.create(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus();

        try {

            customTableApi.update(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus();

        try {
            int nrDeleted = customTableApi.remove(dto);
            result.setNrAffected(nrDeleted);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomTableDataResponseDto list(String customTableCode, PagingAndFiltering pagingAndFiltering) {

        CustomTableDataResponseDto result = new CustomTableDataResponseDto();

        try {

            return customTableApi.list(customTableCode, pagingAndFiltering);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;

    }

    @Override
    public ActionStatus createOrUpdate(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus();

        try {

            customTableApi.createOrUpdate(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus();

        try {
            customTableApi.enableDisable(dto, true);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus();

        try {
            customTableApi.enableDisable(dto, false);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomTableDataResponseDto listFromWrapper(CustomTableWrapperDto customTableWrapperDto) {
        CustomTableDataResponseDto result = new CustomTableDataResponseDto();

        try {

            return customTableApi.listFromWrapper(customTableWrapperDto);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String customTableCode, PagingAndFiltering pagingAndFiltering) {
        ActionStatus result = new ActionStatus();

        try {
            int nrDeleted = customTableApi.remove(customTableCode, pagingAndFiltering);
            result.setNrAffected(nrDeleted);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}