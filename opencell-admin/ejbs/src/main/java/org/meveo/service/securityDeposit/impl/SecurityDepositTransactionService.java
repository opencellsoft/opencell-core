package org.meveo.service.securityDeposit.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.model.securityDeposit.SecurityDepositTransaction;
import org.meveo.service.base.BusinessService;

@Stateless
public class SecurityDepositTransactionService extends BusinessService<SecurityDepositTransaction> {

    private static final String NAMEDQUERY_GET_SECURITYDEPOSIT_CODE_BY_AO_ID = "SecurityDepositTransaction.getSecurityDepositCodesByAoId";
    private static final String PARAM_AO_ID = "aoId";

    /**
     * Gets the payment methods.
     *
     * @return the payment methods
     */
    @SuppressWarnings("unchecked")
    public List<SecurityDepositTransaction> getSecurityDepositTransactionBySecurityDepositId(Long securityDepositId) {
        Query query = this.getEntityManager().createQuery("select m from SecurityDepositTransaction m where m.securityDeposit.id=:id", SecurityDepositTransaction.class);
        query.setParameter("id", securityDepositId);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting 'Security Deposit Transaction' list by 'Security Deposit'", e);
            return new ArrayList<>();
        }
    }

    public List<String> getSecurityDepositCodesByAoIds(Long aoId) {
        return this.getEntityManager().createNamedQuery(NAMEDQUERY_GET_SECURITYDEPOSIT_CODE_BY_AO_ID)
                .setParameter(PARAM_AO_ID, aoId).getResultList();
    }
    
}