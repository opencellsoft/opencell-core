package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.WalletTemplate;
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
	
	public Tax findByCode(EntityManager em, String code) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "t");
			qb.addCriterion("code", "=", code, false);
			return (Tax) qb.getQuery(em).getSingleResult();
		} catch (NoResultException ne) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WalletTemplate> findStartsWithCode(EntityManager em, String walletId,BillingWalletTypeEnum walletType) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "t");
			qb.like("code", walletId, 1, false);
			if(walletType!=null){
				qb.addCriterionEnum("walletType", walletType);
			}
			return (List<WalletTemplate>) qb.getQuery(em).getResultList();
		} catch (NoResultException ne) {
			return null;
		}
	}
}
