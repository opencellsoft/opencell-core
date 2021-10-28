package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class DunningPolicyLevelService extends PersistenceService<DunningPolicyLevel> {

    public List<DunningPolicyLevel> findByPolicyID(Long policyID) {
        return getEntityManager().createNamedQuery("DunningPolicyLevel.findDunningPolicyLevels", DunningPolicyLevel.class)
                .setParameter("policyId", policyID)
                .getResultList();
    }
}
