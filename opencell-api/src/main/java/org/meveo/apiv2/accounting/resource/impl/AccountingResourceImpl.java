package org.meveo.apiv2.accounting.resource.impl;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.serverError;
import static org.meveo.commons.utils.ParamBeanFactory.getAppScopeInstance;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import org.meveo.apiv2.accounting.AuxiliaryAccount;
import org.meveo.apiv2.accounting.ImmutableAuxiliaryAccount;
import org.meveo.apiv2.accounting.resource.AccountingResource;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.AccountingCode;

import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

public class AccountingResourceImpl implements AccountingResource {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private OCCTemplateService occTemplateService;

    private static final String OCC_CODE_KEY = "accountOperationsGenerationJob.occCode";
    private static final String OCC_DEFAULT_CODE = "INV_STD";

    @Override
    public Response getAuxiliaryAccount(String customerAccountCode) {
        CustomerAccount customerAccount =
                ofNullable(customerAccountService.findByCode(customerAccountCode, asList("generalClientAccount")))
                        .orElseThrow(() -> new NotFoundException("Customer account not found"));
        FinanceSettings financeSettings = ofNullable(financeSettingsService.findLastOne())
                .orElseThrow(() -> new NotFoundException("No finance settings found"));
        AuxiliaryAccounting auxiliaryAccounting = financeSettings.getAuxiliaryAccounting();
        if(auxiliaryAccounting != null && auxiliaryAccounting.isUseAuxiliaryAccounting()) {
            try {
                return ok()
                        .entity(generateAuxiliaryAccountInfo(customerAccount, auxiliaryAccounting))
                        .build();
            } catch (Exception exception) {
                return serverError()
                        .entity("{\"actionStatus\":{\"status\":\"FAIL\",\"message\":\"Auxiliary account information not correctly evaluated\", \"error\":\"" + exception.getMessage() + "\"}}")
                        .build();
            }
        } else {
            return ok()
                    .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Auxiliary accounts are not configured from finance settings\"}}")
                    .build();
        }
    }

    private AuxiliaryAccount generateAuxiliaryAccountInfo(CustomerAccount customerAccount,
                                                          AuxiliaryAccounting auxiliaryAccounting) {
        Map<Object, Object> context = new HashMap<>();
        context.put("ca", customerAccount);
        context.put("gca", getGeneralClientAccounting(customerAccount));
        String auxiliaryAccountCodeEl = auxiliaryAccounting.getAuxiliaryAccountCodeEl() != null
                && !auxiliaryAccounting.getAuxiliaryAccountCodeEl().isBlank()
                ? auxiliaryAccounting.getAuxiliaryAccountCodeEl() : buildDefaultAuxiliaryCodeEL(customerAccount.getIsCompany());
        String auxiliaryAccountLabelEl = auxiliaryAccounting.getAuxiliaryAccountLabelEl() != null
                && !auxiliaryAccounting.getAuxiliaryAccountLabelEl().isBlank()
                ? auxiliaryAccounting.getAuxiliaryAccountLabelEl() : buildDefaultAuxiliaryLabelEL(customerAccount.getIsCompany());
        String auxiliaryAccountCode =
                evaluateExpression(auxiliaryAccountCodeEl, context, String.class);
        String auxiliaryAccountLabel =
                evaluateExpression(auxiliaryAccountLabelEl, context, String.class);
        return buildResponse(customerAccount, auxiliaryAccountCode, auxiliaryAccountLabel);
    }

    private AccountingCode getGeneralClientAccounting(CustomerAccount customerAccount) {
        if (customerAccount.getGeneralClientAccount() != null) {
            return customerAccount.getGeneralClientAccount();
        } else {
            String occTemplateCode =
                    getAppScopeInstance().getProperty(OCC_CODE_KEY, OCC_DEFAULT_CODE);
            OCCTemplate occTemplate = occTemplateService.findByCode(occTemplateCode, asList("accountingCode"));
            return ofNullable(occTemplate)
                    .map(OCCTemplate::getAccountingCode)
                    .orElse(null);
        }
    }

    private String buildDefaultAuxiliaryCodeEL(boolean isCompany) {
        if(isCompany) {
            return "#{gca.code.substring(0, 3)}#{ca.description}";
        } else {
            return "#{gca.code.substring(0, 3)}#{ca.name.lastName}";
        }
    }

    private String buildDefaultAuxiliaryLabelEL(boolean isCompany) {
        if(isCompany) {
            return "#{ca.description}";
        } else {
            return "#{ca.name.firstName} #{ca.name.lastName}";
        }
    }

    private AuxiliaryAccount buildResponse(CustomerAccount customerAccount,
                                           String auxiliaryAccountCode, String auxiliaryAccountLabel) {
        Resource customerAccountResource = ImmutableResource.builder()
                .id(customerAccount.getId())
                .code(customerAccount.getCode())
                .build();
        return ImmutableAuxiliaryAccount.builder()
                .customerAccount(customerAccountResource)
                .auxiliaryAccountCode(auxiliaryAccountCode)
                .auxiliaryAccountLabel(auxiliaryAccountLabel)
                .build();
    }
}