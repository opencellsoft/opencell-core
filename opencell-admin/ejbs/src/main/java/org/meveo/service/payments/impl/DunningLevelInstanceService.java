package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class DunningLevelInstanceService extends PersistenceService<DunningLevelInstance> {

    public DunningLevelInstance findByPolicyLevelId(Long policyLevelId) {
        try {
            return getEntityManager()
                    .createNamedQuery("DunningLevelInstance.findByPolicyLevelId", entityClass)
                    .setParameter("policyLevelId", policyLevelId)
                    .getSingleResult();
        } catch (Exception exception) {
            return null;
        }
    }
    
    public DunningLevelInstance findByCurrentLevelSequence(DunningCollectionPlan collectionPlan) {
        try {
            return getEntityManager()
                    .createNamedQuery("DunningLevelInstance.findByCurrentLevelSequence", entityClass)
                    .setParameter("collectionPlan", collectionPlan)
                    .setParameter("sequence", collectionPlan.getCurrentDunningLevelSequence())
                    .getSingleResult();
        } catch (Exception exception) {
            return null;
        }
    }

    public DunningLevelInstance findLastLevelInstance(DunningCollectionPlan collectionPlan) {
        try {
            return getEntityManager()
                    .createNamedQuery("DunningLevelInstance.findLastLevelInstance", entityClass)
                    .setParameter("collectionPlan", collectionPlan)
                    .getSingleResult();
        } catch (Exception exception) {
            return null;
        }
    }
}
