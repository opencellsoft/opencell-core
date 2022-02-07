package org.meveo.service.securityDeposit.impl;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceInstanceService;

@Stateless
public class SecurityDepositService extends BusinessService<SecurityDeposit> {

    @Inject
    private CurrencyService currencyService;

    @Inject
    private AuditLogService auditLogService;
    
    @Inject
    private FinanceSettingsService financeSettingsService;
    
    @Inject
    private ServiceInstanceService serviceInstanceService;
    
    public BigDecimal sumAmountPerCustomer(CustomerAccount customerAccount) {
        return getEntityManager()
                .createNamedQuery("SecurityDeposit.sumAmountPerClient", BigDecimal.class)
                .setParameter("customerAccount", customerAccount)
                .getSingleResult();
    }
    
    public Long countPerTemplate(SecurityDepositTemplate template) {
        return getEntityManager()
                .createNamedQuery("SecurityDeposit.countPerTemplate", Long.class)
                .setParameter("template", template)
                .getSingleResult();
    }
    
    public void checkParameters(SecurityDeposit securityDeposit,SecurityDepositInput securityDepositInput, BigDecimal oldAmountSD)
    {
        FinanceSettings financeSettings = financeSettingsService.findLastOne();
        if(securityDeposit.getCurrency() == null)
            throw new EntityDoesNotExistsException("currency does not exist.");
        if(!financeSettings.isAutoRefund() && (securityDepositInput.getValidityDate() != null || securityDepositInput.getValidityPeriod() != null || securityDepositInput.getValidityPeriodUnit() != null))
            throw new InvalidParameterException("the option 'Allow auto refund' need to be checked");
        if(!SecurityDepositStatusEnum.NEW.equals(securityDeposit.getStatus()) && !SecurityDepositStatusEnum.HOLD.equals(securityDeposit.getStatus()))
            securityDeposit.setAmount(oldAmountSD);
        if(securityDeposit.getServiceInstance() != null && securityDeposit.getSubscription() != null){           
            ServiceInstance serviceInstance = serviceInstanceService.refreshOrRetrieve(securityDeposit.getServiceInstance());
            if(serviceInstance.getSubscription() != null && serviceInstance.getSubscription().getId() != securityDeposit.getSubscription().getId()){
                throw new InvalidParameterException("ServiceInstance must have the same chosen in subscription");
            }
        }
    }
}
