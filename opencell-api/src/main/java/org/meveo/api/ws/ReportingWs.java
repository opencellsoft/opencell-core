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

package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.GetMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.dto.response.dwh.GetMeasuredValueResponse;
import org.meveo.model.dwh.MeasurementPeriodEnum;

/**
 * @author Edward P. Legaspi
 **/
@WebService
@Deprecated
public interface ReportingWs extends IBaseWs {

    @WebMethod
    ActionStatus createMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

    @WebMethod
    ActionStatus updateMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

    @WebMethod
    ActionStatus createOrUpdateMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

    /**
     * Find a Measurable quantity by its code
     * 
     * @param code Measurable quantity code
     * @return Request processing status and Measurable quantity information
     */
    @WebMethod
    GetMeasurableQuantityResponse findMeasurableQuantity(@WebParam(name = "code") String code);

    /**
     * Enable a Measurable quantity by its code
     * 
     * @param code Measurable quantity code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableMeasurableQuantity(@WebParam(name = "code") String code);

    /**
     * Disable a Measurable quantity by its code
     * 
     * @param code Measurable quantity code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableMeasurableQuantity(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createChart(@WebParam(name = "chart") ChartDto postData);

    @WebMethod
    ActionStatus updateChart(@WebParam(name = "chart") ChartDto postData);

    @WebMethod
    ActionStatus createOrUpdateChart(@WebParam(name = "chart") ChartDto postData);

    @WebMethod
    ActionStatus createBarChart(@WebParam(name = "barChart") BarChartDto postData);

    @WebMethod
    ActionStatus updateBarChart(@WebParam(name = "barChart") BarChartDto postData);

    @WebMethod
    ActionStatus createOrUpdateBarChart(@WebParam(name = "barChart") BarChartDto postData);

    @WebMethod
    ActionStatus createPieChart(@WebParam(name = "pieChart") PieChartDto postData);

    @WebMethod
    ActionStatus updatePieChart(@WebParam(name = "pieChart") PieChartDto postData);

    @WebMethod
    ActionStatus createOrUpdatePieChart(@WebParam(name = "pieChart") PieChartDto postData);

    @WebMethod
    ActionStatus createLineChart(@WebParam(name = "lineChart") LineChartDto postData);

    @WebMethod
    ActionStatus updateLineChart(@WebParam(name = "lineChart") LineChartDto postData);

    @WebMethod
    ActionStatus createOrUpdateLineChart(@WebParam(name = "lineChart") LineChartDto postData);

    @WebMethod
    ActionStatus removeChart(@WebParam(name = "chartCode") String chartCode);

    @WebMethod
    GetChartResponse findChart(@WebParam(name = "chartCode") String chartCode);

    /**
     * Enable a Chart by its code
     * 
     * @param code Chart code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableChart(@WebParam(name = "code") String code);

    /**
     * Disable a Chart by its code
     * 
     * @param code Chart code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableChart(@WebParam(name = "code") String code);

    @WebMethod
    GetMeasuredValueResponse findMVByDateAndPeriod(@WebParam(name = "code") String code, @WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate,
            @WebParam(name = "period") MeasurementPeriodEnum period, @WebParam(name = "mqCode") String mqCode);

}
