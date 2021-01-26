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

package org.meveo.api.rest.billing.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.billing.InvoicingApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.billing.InvoiceValidationDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.InvoicingRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoicingRsImpl extends BaseRs implements InvoicingRs {

    @Inject
    private InvoicingApi invoicingApi;

    @Override
    public ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.info("createBillingRun request={}", createBillingRunDto);
        try {
            long billingRunId = invoicingApi.createBillingRun(createBillingRunDto);
            result.setMessage(billingRunId + "");
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId) {
        GetBillingRunInfoResponseDto result = new GetBillingRunInfoResponseDto();
        log.info("getBillingRunInfo request={}", billingRunId);
        try {

            result.setBillingRunDto(invoicingApi.getBillingRunInfo(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getBillingRunInfo Response={}", result);
        return result;
    }

    @Override
    public GetBillingAccountListInRunResponseDto getBillingAccountListInRun(Long billingRunId) {
        GetBillingAccountListInRunResponseDto result = new GetBillingAccountListInRunResponseDto();
        log.info("getBillingAccountListInRun request={}", billingRunId);
        try {

            result.setBillingAccountsDto(invoicingApi.getBillingAccountListInRun(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getBillingAccountListInRun Response={}", result);
        return result;
    }

    @Override
    public GetPreInvoicingReportsResponseDto getPreInvoicingReport(Long billingRunId) {
        GetPreInvoicingReportsResponseDto result = new GetPreInvoicingReportsResponseDto();
        log.info("getPreInvoicingReport request={}", billingRunId);
        try {

            result.setPreInvoicingReportsDTO(invoicingApi.getPreInvoicingReport(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getPreInvoicingReport Response={}", result);
        return result;
    }

    @Override
    public GetPostInvoicingReportsResponseDto getPostInvoicingReport(Long billingRunId) {
        GetPostInvoicingReportsResponseDto result = new GetPostInvoicingReportsResponseDto();
        log.info("getPreInvoicingReport request={}", billingRunId);
        try {

            result.setPostInvoicingReportsDTO(invoicingApi.getPostInvoicingReport(billingRunId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getPostInvoicingReport Response={}", result);
        return result;
    }

    @Override
    public ActionStatus validateBillingRun(Long billingRunId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.info("validateBillingRun request={}", billingRunId);
        try {

            invoicingApi.validateBillingRun(billingRunId);

        } catch (Exception e) {
            processException(e, result);
        }
        log.info("validateBillingRun Response={}", result);
        return result;
    }

    @Override
    public ActionStatus cancelBillingRun(Long billingRunId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.info("cancelBillingRun request={}", billingRunId);
        try {

            invoicingApi.cancelBillingRun(billingRunId);

        } catch (Exception e) {
            processException(e, result);
        }
        log.info("cancelBillingRun Response={}", result);
        return result;
    }

	@Override
	public ActionStatus rebuildInvoice(InvoiceValidationDto invoiceValidationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            invoicingApi.rebuildInvoice(null, invoiceValidationDto.getInvoices());
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("rebuildInvoice Response={}", result);
        return result;
	}

	@Override
	public ActionStatus rejectInvoice(Long billingRunId, InvoiceValidationDto invoiceValidationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("rejectInvoice request={}", billingRunId);
        try {
            invoicingApi.rejectInvoice(billingRunId, invoiceValidationDto.getInvoices());
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("rejectInvoice Response={}", result);
        return result;
	}

	@Override
	public ActionStatus validateInvoice(Long billingRunId, InvoiceValidationDto invoiceValidationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("validateInvoice request={}", billingRunId);
        try {
            invoicingApi.validateInvoice(billingRunId, invoiceValidationDto.getInvoices());
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("validateInvoice Response={}", result);
        return result;
	}

	@Override
	public ActionStatus moveInvoice(Long billingRunId, InvoiceValidationDto invoiceValidationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("moveInvoice request={}", billingRunId);
        try {
            invoicingApi.moveInvoice(billingRunId, invoiceValidationDto.getInvoices());
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("moveInvoice Response={}", result);
        return result;
	}

	@Override
	public ActionStatus cancelInvoice(Long billingRunId, InvoiceValidationDto invoiceValidationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("cancelInvoice request={}", billingRunId);
        try {
            invoicingApi.cancelInvoice(billingRunId, invoiceValidationDto.getInvoices(), invoiceValidationDto.getDeleteCanceledInvoices());
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("cancelInvoice Response={}", result);
        return result;
	}

	@Override
	public ActionStatus canceledInvoices(Long billingRunId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.debug("canceledInvoices request={}", billingRunId);
        try {
            invoicingApi.canceledInvoices(billingRunId);
        } catch (Exception e) {
            processException(e, result);
        }
        log.debug("canceledInvoices Response={}", result);
        return result;
	}

}
