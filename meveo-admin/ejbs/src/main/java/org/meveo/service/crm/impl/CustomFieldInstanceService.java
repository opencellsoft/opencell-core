package org.meveo.service.crm.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldInstanceService extends BusinessService<CustomFieldInstance> {

	public CustomFieldInstance findByCodeAndAccount(String code, IEntity t,Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
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
	
	public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, String value, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("stringValue", "=", value, true);
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
	
	public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, Date value, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterionDate("dateValue", value);
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
	
	public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, Long value, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("longValue", "=", value, true);
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
	
	public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, Double value, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("doubleValue", "=", value, true);
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
	
	public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, String stringValue, Date dateValue, Long longValue, Double doubleValue, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		if (!StringUtils.isBlank(stringValue)) {
			qb.addCriterion("stringValue", "=", stringValue, true);
		}
		if (dateValue != null) {
			qb.addCriterionDate("dateValue", dateValue);
		}
		if (longValue != null) {
			qb.addCriterion("longValue", "=", longValue, true);
		}
		if (doubleValue != null) {
			qb.addCriterion("doubleValue", "=", doubleValue, true);
		}
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
	
	public CustomFieldInstance findByCodeAndAccountAndValue(String code, String accountType, String stringValue, Date dateValue, Long longValue, Double doubleValue, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		if (!StringUtils.isBlank(stringValue)) {
			qb.addCriterion("stringValue", "=", stringValue, true);
		}
		if (dateValue != null) {
			qb.addCriterionDate("dateValue", dateValue);
		}
		if (longValue != null) {
			qb.addCriterion("longValue", "=", longValue, true);
		}
		if (doubleValue != null) {
			qb.addCriterion("doubleValue", "=", doubleValue, true);
		}
		if (!StringUtils.isBlank(accountType)) {
			qb.addCriterion("account.accountType", "=", accountType, true);
		}

		try {
			return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
