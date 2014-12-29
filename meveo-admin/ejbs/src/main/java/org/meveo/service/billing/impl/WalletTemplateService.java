package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.WalletTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WalletTemplateService extends PersistenceService<WalletTemplate> {

	@SuppressWarnings("unchecked")
	public List<WalletTemplate> listByProvider(Provider provider) {
		QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "w");
		qb.addCriterionEntity("provider", provider);

		try {
			return (List<WalletTemplate>) qb.getQuery(getEntityManager())
					.getResultList();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}
}
