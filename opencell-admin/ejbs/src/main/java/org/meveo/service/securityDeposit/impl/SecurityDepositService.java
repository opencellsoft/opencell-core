package org.meveo.service.securityDeposit.impl;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import java.util.Date;

@Stateless
public class SecurityDepositService extends BusinessService<SecurityDeposit> {

    @Inject
    private CurrencyService currencyService;

    @Inject
    private AuditLogService auditLogService;
    
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
    
    public SecurityDeposit update(SecurityDeposit entity) {
        entity.setCurrency(currencyService.findById(entity.getCurrency().getId()));
        //checkParameters(entity);
        SecurityDeposit updatedSecurityDeposit =  super.update(entity);
        auditLogService.trackOperation("UPDATE", new Date(), updatedSecurityDeposit, updatedSecurityDeposit.getCode());
        return updatedSecurityDeposit;
    }
}
