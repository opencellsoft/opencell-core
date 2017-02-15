package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class WalletTemplateService extends BusinessService<WalletTemplate> {

	@SuppressWarnings("unchecked")
	public List<WalletTemplate> findStartsWithCode(String walletId,BillingWalletTypeEnum walletType) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "t");
			qb.like("code", walletId, QueryLikeStyleEnum.MATCH_BEGINNING, false);
			if(walletType!=null){
				qb.addCriterionEnum("walletType", walletType);
			}
			return (List<WalletTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException ne) {
			return null;
		}
	}
}
