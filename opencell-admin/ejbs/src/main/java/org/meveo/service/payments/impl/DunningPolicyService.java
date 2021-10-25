package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningPolicy;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class DunningPolicyService extends PersistenceService<DunningPolicy> {

    public DunningPolicy findByName(String policyName) {
        return getEntityManager().createNamedQuery("DunningPolicy.findByName", DunningPolicy.class)
                .setParameter("policyName", policyName)
                .getSingleResult();
    }
}
