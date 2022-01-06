package org.meveo.service.securityDeposit.impl;

import java.math.BigDecimal;

import javax.ejb.Stateless;

import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class SecurityDepositService extends BusinessService<SecurityDeposit> {

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
}
