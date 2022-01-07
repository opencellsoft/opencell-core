package org.meveo.service.payments.impl;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class DunningActionInstanceService extends PersistenceService<DunningActionInstance> {
	
	public DunningActionInstance findByCode(String code) {
		QueryBuilder qb = new QueryBuilder(DunningActionInstance.class, "d");
		qb.addCriterion("code", "=", code, true);

        try {
            return (DunningActionInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
	}

	public int updateStatus(DunningActionInstanceStatusEnum actionStatus, DunningLevelInstance dunningLevelInstance) {
        return getEntityManager()
                .createNamedQuery("DunningActionInstance.updateStatus")
                .setParameter("actionStatus", actionStatus)
                .setParameter("dunningLevelInstance", dunningLevelInstance)
                .executeUpdate();
    }
}
