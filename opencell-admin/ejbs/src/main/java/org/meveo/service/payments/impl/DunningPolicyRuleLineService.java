package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningPolicyRuleLine;
import org.meveo.service.base.PersistenceService;

import java.util.List;

import javax.ejb.Stateless;

@Stateless
public class DunningPolicyRuleLineService extends PersistenceService<DunningPolicyRuleLine> {

    public List<DunningPolicyRuleLine> findByDunningPolicy(long dunningPolicyId) {
        return getEntityManager().createNamedQuery("DunningPolicyRuleLine.findByDunningPolicyId")
                        .setParameter("policyId", dunningPolicyId)
                        .getResultList();
    }

    public List<DunningPolicyRuleLine> findByDunningPolicyRule(long dunningPolicyRuleId) {
        return getEntityManager().createNamedQuery("DunningPolicyRuleLine.findByDunningPolicyRuleId")
                        .setParameter("dunningPolicyRuleId", dunningPolicyRuleId)
                        .getResultList();
    }    
}