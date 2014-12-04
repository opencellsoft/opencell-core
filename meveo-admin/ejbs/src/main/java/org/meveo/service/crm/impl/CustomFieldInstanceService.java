package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.AccountEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldInstanceService extends
		BusinessService<CustomFieldInstance> {

	public CustomFieldInstance findByCodeAndAccount(String code,
			AccountEntity account) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("account", account);

		try {
			return (CustomFieldInstance) qb.getQuery(getEntityManager())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
