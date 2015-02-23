package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.AccountEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldInstanceService extends BusinessService<CustomFieldInstance> {

	public CustomFieldInstance findByCodeAndAccount(String code, IEntity t) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		if (t instanceof AccountEntity) {
			qb.addCriterionEntity("account", t);
		} else if (t instanceof Subscription) {
			qb.addCriterionEntity("subscription", t);
		} else if (t instanceof Access) {
			qb.addCriterionEntity("access", t);
		}
		
		try {
			return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
