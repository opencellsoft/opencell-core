package org.meveo.service.payments.impl;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.dunning.DunningSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningAgent entity.
 * It extends {@link PersistenceService} class
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningSettingsService extends BusinessService<DunningSettings> {

	public DunningSettings duplicate(DunningSettings dunningSettings) {
		var duplicate = new DunningSettings(dunningSettings);
		duplicate.setCode(this.findDuplicateCode(duplicate));
		this.create(duplicate);
		return duplicate;
	}

	/**
	 * Find the last dunning settings
	 * @return {@link DunningSettings}
	 */
	public DunningSettings findLastOne() {
		try {
			TypedQuery<DunningSettings> query = getEntityManager().createQuery("from DunningSettings f order by f.id desc", entityClass).setMaxResults(1);
			return query.getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} found", getEntityClass().getSimpleName());
			return null;
		}
	}
}
