package org.meveo.service.payments.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CreditCategory;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CreditCategoryService extends PersistenceService<CreditCategory> {

	public CreditCategory findByCode(String code, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CreditCategory.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);

		try {
			return (CreditCategory) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
