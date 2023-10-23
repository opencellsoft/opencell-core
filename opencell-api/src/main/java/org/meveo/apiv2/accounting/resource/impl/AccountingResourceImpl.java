package org.meveo.apiv2.accounting.resource.impl;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.serverError;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.accounting.AuxiliaryAccount;
import org.meveo.apiv2.accounting.ImmutableAuxiliaryAccount;
import org.meveo.apiv2.accounting.resource.AccountingResource;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;

import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.validation.ValidationByNumberCountryService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import java.util.Map;

@Interceptors({ WsRestApiInterceptor.class })
public class AccountingResourceImpl implements AccountingResource {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private ValidationByNumberCountryService validationByNumberCountryService;
    
    @Override
    public Response getAuxiliaryAccount(String customerAccountCode) {
        CustomerAccount customerAccount =
                ofNullable(customerAccountService.findByCode(customerAccountCode, asList("generalClientAccount")))
                        .orElseThrow(() -> new NotFoundException("Customer account not found"));
        FinanceSettings financeSettings = ofNullable(financeSettingsService.getFinanceSetting())
                .orElseThrow(() -> new NotFoundException("No finance settings found"));
        AuxiliaryAccounting auxiliaryAccounting = financeSettings.getAuxiliaryAccounting();
        if(auxiliaryAccounting != null && auxiliaryAccounting.isUseAuxiliaryAccounting()) {
            try {
                Map<String, String> result =
                        financeSettingsService.generateAuxiliaryAccountInfo(customerAccount, auxiliaryAccounting);
                return ok()
                        .entity(buildResponse(customerAccount, result))
                        .build();
            } catch (Exception exception) {
                return serverError()
                        .entity("{\"actionStatus\":{\"status\":\"FAIL\",\"message\":\"Auxiliary account information not correctly evaluated\", \"error\":\"" + exception.getMessage() + "\"}}")
                        .build();
            }
        } else {
            throw new NotFoundException("Auxiliary accounts are not set in Finance settings");
        }
    }
    
    @Override
    public Response getValByValNbContryCode(String vatNumber, String countryCode) {
        boolean valueValideNodeBoolean = false; 
        
        try {
            valueValideNodeBoolean = validationByNumberCountryService.getValByValNbCountryCode(vatNumber, countryCode); 
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        if (valueValideNodeBoolean) {
            return Response.ok(result).build();
        }

        throw new BadRequestException("Invalid VAT");
    }   

    private AuxiliaryAccount buildResponse(CustomerAccount customerAccount, Map<String, String> accountingResult) {
        Resource customerAccountResource = ImmutableResource.builder()
                .id(customerAccount.getId())
                .code(customerAccount.getCode())
                .build();
        return ImmutableAuxiliaryAccount.builder()
                .customerAccount(customerAccountResource)
                .auxiliaryAccountCode(accountingResult.get("auxiliaryAccountCode"))
                .auxiliaryAccountLabel(accountingResult.get("auxiliaryAccountLabel"))
                .build();
    }
}