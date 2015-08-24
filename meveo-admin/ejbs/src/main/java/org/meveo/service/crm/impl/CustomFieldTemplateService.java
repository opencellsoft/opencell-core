package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldTemplateService extends BusinessService<CustomFieldTemplate> {

    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByJobName(String jobName) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, getCurrentProvider());
        qb.addCriterion("accountLevel", "=", AccountLevelEnum.TIMER, true);
        qb.addCriterionWildcard("code", jobName+"_*", false);
        try {
            return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

	@SuppressWarnings("unchecked")
	public List<CustomFieldTemplate> findByAccountLevel(AccountLevelEnum accountLevel) {
		QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, getCurrentProvider());
		qb.addCriterion("accountLevel", "=", accountLevel, true);
		qb.addOrderCriterion("description",true);
		try {
			return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CustomFieldTemplate> findByAccountLevel(AccountLevelEnum accountLevel, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c");
		qb.addCriterion("c.accountLevel", "=", accountLevel, true);
		qb.addCriterionEntity("c.provider", provider);
		try {
			return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public CustomFieldTemplate findByCodeAndAccountLevel(String code, AccountLevelEnum accountLevel, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, provider);
		qb.addCriterion("code", "=", code, true);
		qb.addCriterion("accountLevel", "=", accountLevel, true);

		try {
			return (CustomFieldTemplate) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

}
