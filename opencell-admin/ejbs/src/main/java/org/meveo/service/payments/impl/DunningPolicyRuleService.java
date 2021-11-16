package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningPolicyRule;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class DunningPolicyRuleService extends PersistenceService<DunningPolicyRule> {

    public List<DunningPolicyRule> findByDunningPolicy(long dunningPolicyId) {
        return getEntityManager().createNamedQuery("DunningPolicyRule.findByDunningPolicyId")
                        .setParameter("policyId", dunningPolicyId)
                        .getResultList();
    }
}