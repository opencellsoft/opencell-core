package org.meveo.api.rest.invoice.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.InvoiceValidationRuleDto;
import org.meveo.api.invoice.InvoiceValidationRulesApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.InvoiceValidationRulesRS;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceValidationRulesRSImpl extends BaseRs implements InvoiceValidationRulesRS {

    @Inject
    private InvoiceValidationRulesApi invoiceValidationRulesApi;

    @Override
    public ActionStatus create(InvoiceValidationRuleDto invoiceValidationRuleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceValidationRulesApi.createInvoiceValidationRule(invoiceValidationRuleDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(Long invoiceValidationRuleId, InvoiceValidationRuleDto invoiceValidationRuleDto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceValidationRulesApi.updateInvoiceValidationRule(invoiceValidationRuleId, invoiceValidationRuleDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
