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

    private static final String NAMEDQUERY_COUNT_EXISTENCE_BY_AO_IDS = "SecurityDepositTransaction.countExistenceByAoIds";
    private static final String PARAM_AO_IDS = "aoIds";

    /**
     * Gets the payment methods.
     *
     * @param billingAccount the billing account
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

    public boolean checkExistanceByAoIds(List<Long> aoIds) {
        return (Long) this.getEntityManager().createNamedQuery(NAMEDQUERY_COUNT_EXISTENCE_BY_AO_IDS)
                .setParameter(PARAM_AO_IDS, aoIds).getSingleResult() > 0;
    }
    
}