package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class DunningLevelInstanceService extends PersistenceService<DunningLevelInstance> {

    public DunningLevelInstance findByPolicyLevelId(long policyLevelId) {
        try {
            return getEntityManager()
                    .createNamedQuery("DunningLevelInstance.findByPolicyLevelId", DunningLevelInstance.class)
                    .setParameter("policyLevelId", policyLevelId)
                    .getSingleResult();
        } catch (Exception exception) {
            return null;
        }
    }
}
