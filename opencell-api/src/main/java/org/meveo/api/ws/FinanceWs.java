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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultsResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.1
 */
@WebService
@Deprecated
public interface FinanceWs extends IBaseWs {

    @WebMethod
    ActionStatus createRevenueRecognitionRule(@WebParam(name = "revenueRecognitionRule") RevenueRecognitionRuleDto moduleDto);

    @WebMethod
    ActionStatus updateRevenueRecognitionRule(@WebParam(name = "revenueRecognitionRule") RevenueRecognitionRuleDto moduleDto);

    @WebMethod
    ActionStatus deleteRevenueRecognitionRule(@WebParam(name = "code") String code);

    @WebMethod
    RevenueRecognitionRuleDtosResponse listRevenueRecognitionRules();

    @WebMethod
    RevenueRecognitionRuleDtoResponse getRevenueRecognitionRule(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createOrUpdateRevenueRecognitionRule(@WebParam(name = "revenueRecognitionRule") RevenueRecognitionRuleDto moduleDto);

    /**
     * Enable a Revenue recognition rule by its code
     * 
     * @param code Revenue recognition rule code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableRevenueRecognitionRule(@WebParam(name = "code") String code);

    /**
     * Disable a Revenue recognition rule by its code
     * 
     * @param code Revenue recognition rule code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableRevenueRecognitionRule(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createReportExtract(@WebParam(name = "reportExtract") ReportExtractDto postData);

    @WebMethod
    ActionStatus updateReportExtract(@WebParam(name = "reportExtract") ReportExtractDto postData);

    @WebMethod
    ActionStatus createOrUpdateReportExtract(@WebParam(name = "reportExtract") ReportExtractDto postData);

    /**
     * Enable a Report extract by its code
     * 
     * @param code Report extract code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableReportExtract(@WebParam(name = "code") String code);

    /**
     * Disable a Report extract by its code
     * 
     * @param code Report extract code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableReportExtract(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus removeReportExtract(@WebParam(name = "reportExtractCode") String reportExtractCode);

    @WebMethod
    ReportExtractsResponseDto listReportExtract(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    @WebMethod
    ReportExtractResponseDto findReportExtract(@WebParam(name = "reportExtractCode") String reportExtractCode);

    @WebMethod
    ReportExtractExecutionResultResponseDto runReportExtract(@WebParam(name = "runReport") RunReportExtractDto postData);
    
    @WebMethod 
    ReportExtractExecutionResultsResponseDto listReportExtractRunHistory(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);
    
    /**
     * Finds and returns the ReportExtract history of a given code.
     * 
     * @param id report extract execution id
     * @return report extract execution history
     */
    @WebMethod
    ReportExtractExecutionResultResponseDto findReportExtractHistoryById(@WebParam(name = "id") Long id);
    
    /**
     * Finds and returns a list of ReportExtract history for a given code.
     * 
     * @param code report extract execution code
     * @return list of report extract execution history
     */
    @WebMethod
    ReportExtractExecutionResultsResponseDto findReportExtractHistoryByCode(@WebParam(name = "code") String code);
    
}
