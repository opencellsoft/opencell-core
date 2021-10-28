package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class DunningPolicyService extends PersistenceService<DunningPolicy> {

    public DunningPolicy findByName(String policyName) {
        try {
            return getEntityManager().createNamedQuery("DunningPolicy.findByName", DunningPolicy.class)
                    .setParameter("policyName", policyName)
                    .getSingleResult();
        } catch (NoResultException noResultException) {
            throw new BusinessException("Dunning policy does not exits");
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
    }
}
