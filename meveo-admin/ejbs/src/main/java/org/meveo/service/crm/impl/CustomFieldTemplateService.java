package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldTemplateService extends BusinessService<CustomFieldTemplate> {

	@SuppressWarnings("unchecked")
	public List<CustomFieldTemplate> findByAccountLevel(AccountLevelEnum accountLevel) {
		QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, getCurrentProvider());
		qb.addCriterion("accountLevel", "=", accountLevel, true);
		try {
			return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
