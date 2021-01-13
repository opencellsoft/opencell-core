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

package org.meveo.api.rest.dwh.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dwh.GetListMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.GetMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.MeasuredValueDto;
import org.meveo.api.dwh.MeasurableQuantityApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.dwh.MeasurableQuantityRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.dwh.MeasurementPeriodEnum;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
@Deprecated
public class MeasurableQuantityRsImpl extends BaseRs implements MeasurableQuantityRs {

    @Inject
    private MeasurableQuantityApi measurableQuantityApi;

    @Override
    public ActionStatus create(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * 
     * @param code
     * @param fromDate format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
     * @param toDate format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
     * @param period
     * @param mqCode
     * @return
     */
    @Override
    public Response findMVByDateAndPeriod(String code, Date fromDate, Date toDate, MeasurementPeriodEnum period, String mqCode) {
        Response.ResponseBuilder responseBuilder = null;
        List<MeasuredValueDto> result = new ArrayList<>();

        try {
            result = measurableQuantityApi.findMVByDateAndPeriod(code, fromDate, toDate, period, mqCode);
            responseBuilder = Response.ok();
            responseBuilder.entity(result);
        } catch (MeveoApiException e) {
            log.error(e.getLocalizedMessage());
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
            responseBuilder.entity(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
            responseBuilder.entity(e.getLocalizedMessage());
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public GetMeasurableQuantityResponse find(String code) {
        GetMeasurableQuantityResponse result = new GetMeasurableQuantityResponse();
        try {
            result.setMeasurableQuantityDto(measurableQuantityApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            measurableQuantityApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetListMeasurableQuantityResponse list() {
        GetListMeasurableQuantityResponse result = new GetListMeasurableQuantityResponse();
        try {
            result.setListMeasurableQuantityDto(measurableQuantityApi.list(null));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            measurableQuantityApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            measurableQuantityApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}