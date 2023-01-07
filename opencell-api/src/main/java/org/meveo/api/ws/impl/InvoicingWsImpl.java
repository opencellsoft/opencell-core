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

package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.InvoicingApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.InvoicingWs;

/**
 * @author anasseh
 **/
@WebService(serviceName = "InvoicingWs", endpointInterface = "org.meveo.api.ws.InvoicingWs")
@Interceptors({ WsRestApiInterceptor.class })
public class InvoicingWsImpl extends BaseWs implements InvoicingWs {

    @Inject
    InvoicingApi invoicingApi;

    @Override
    public ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("createBillingRun request={}", createBillingRunDto);
        try {

            long billingRunId = invoicingApi.createBillingRun(createBillingRunDto);
            result.setMessage(billingRunId + "");
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("createBillingRun Response={}", result);
        return result;
    }

    @Override
    public GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId) {
        GetBillingRunInfoResponseDto result = new GetBillingRunInfoResponseDto();
        log.debug("getBillingRunInfo request={}", billingRunId);
        try {

            result.setBillingRunDto(invoicingApi.getBillingRunInfo(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.debug("getBillingRunInfo Response={}", result);
        return result;
    }

    @Override
    public GetBillingAccountListInRunResponseDto getBillingAccountListInRun(Long billingRunId) {
        GetBillingAccountListInRunResponseDto result = new GetBillingAccountListInRunResponseDto();
        log.debug("getBillingAccountListInRun request={}", billingRunId);
        try {

            result.setBillingAccountsDto(invoicingApi.getBillingAccountListInRun(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.debug("getBillingAccountListInRun Response={}", result);
        return result;
    }

    @Override
    public GetPreInvoicingReportsResponseDto getPreInvoicingReport(Long billingRunId) {
        GetPreInvoicingReportsResponseDto result = new GetPreInvoicingReportsResponseDto();
        log.debug("getPreInvoicingReport request={}", billingRunId);
        try {

            result.setPreInvoicingReportsDTO(invoicingApi.getPreInvoicingReport(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.debug("getPreInvoicingReport Response={}", result);
        return result;
    }

    @Override
    public GetPostInvoicingReportsResponseDto getPostInvoicingReport(Long billingRunId) {
        GetPostInvoicingReportsResponseDto result = new GetPostInvoicingReportsResponseDto();
        log.debug("getPreInvoicingReport request={}", billingRunId);
        try {

            result.setPostInvoicingReportsDTO(invoicingApi.getPostInvoicingReport(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.debug("getPostInvoicingReport Response={}", result);
        return result;
    }

    @Override
    public ActionStatus validateBillingRun(Long billingRunId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("validateBillingRun request={}", billingRunId);
        try {

            invoicingApi.validateBillingRun(billingRunId);

        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("validateBillingRun Response={}", result);
        return result;
    }

    @Override
    public ActionStatus cancelBillingRun(Long billingRunId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("cancelBillingRun request={}", billingRunId);
        try {

            invoicingApi.cancelBillingRun(billingRunId);

        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("cancelBillingRun Response={}", result);
        return result;
    }
}