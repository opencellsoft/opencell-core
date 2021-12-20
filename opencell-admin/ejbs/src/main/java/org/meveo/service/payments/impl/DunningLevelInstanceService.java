package org.meveo.service.payments.impl;

import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class DunningLevelInstanceService extends PersistenceService<DunningLevelInstance> {

    public DunningLevelInstance findByLevelId(Long levelId) {
        try {
            return getEntityManager()
                    .createNamedQuery("DunningLevelInstance.findByLevelId", entityClass)
                    .setParameter("levelId", levelId)
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
    
    public DunningLevelInstance findBySequence(DunningCollectionPlan collectionPlan, Integer sequence) {
        try {
            return getEntityManager()
                    .createNamedQuery("DunningLevelInstance.findByCurrentLevelSequence", entityClass)
                    .setParameter("collectionPlan", collectionPlan)
                    .setParameter("sequence", sequence)
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

    public boolean checkDaysOverdueIsAlreadyExist(DunningCollectionPlan collectionPlan, Integer daysOverdue) {
        return getEntityManager()
                .createNamedQuery("DunningLevelInstance.checkDaysOverdueIsAlreadyExist", Long.class)
                .setParameter("collectionPlan", collectionPlan)
                .setParameter("daysOverdue", daysOverdue)
                .getSingleResult() > 0;
    }

    public Long getMinSequenceByDaysOverdue(DunningCollectionPlan collectionPlan, Integer daysOverdue) {
        return getEntityManager()
                .createNamedQuery("DunningLevelInstance.checkDaysOverdueIsAlreadyExist", Long.class)
                .setParameter("collectionPlan", collectionPlan)
                .setParameter("daysOverdue", daysOverdue)
                .getSingleResult();
    }

    public void incrementSequecesGreaterThanDaysOverdue(DunningCollectionPlan collectionPlan, Integer daysOverdue) {
        getEntityManager()
            .createNamedQuery("DunningLevelInstance.incrementSequecesByDaysOverdue")
            .setParameter("collectionPlan", collectionPlan)
            .setParameter("daysOverdue", daysOverdue)
            .executeUpdate();
    }

    public void decrementSequecesGreaterThanDaysOverdue(DunningCollectionPlan collectionPlan, Integer daysOverdue) {
        getEntityManager()
            .createNamedQuery("DunningLevelInstance.decrementSequecesByDaysOverdue")
            .setParameter("collectionPlan", collectionPlan)
            .setParameter("daysOverdue", daysOverdue)
            .executeUpdate();
    }
}
