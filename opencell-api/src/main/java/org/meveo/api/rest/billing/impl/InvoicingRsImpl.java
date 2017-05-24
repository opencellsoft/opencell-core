package org.meveo.api.rest.billing.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.billing.InvoicingApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CreateBillingRunDto;
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

}
