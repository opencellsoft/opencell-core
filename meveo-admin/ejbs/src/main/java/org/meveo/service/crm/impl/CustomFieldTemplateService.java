package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldTemplateService extends BusinessService<CustomFieldTemplate> {

    @Inject
    private CustomFieldsCacheContainerProvider customFieldsCache;

    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByJobName(String jobName) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, getCurrentProvider());
        qb.addCriterion("accountLevel", "=", AccountLevelEnum.TIMER, true);
        qb.addCriterionWildcard("code", jobName + "_*", false);
        return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByAccountLevel(AccountLevelEnum accountLevel) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, getCurrentProvider());
        qb.addCriterion("accountLevel", "=", accountLevel, true);
        qb.addOrderCriterion("description", true);
        return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByAccountLevel(AccountLevelEnum accountLevel, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c");
        qb.addCriterion("c.accountLevel", "=", accountLevel, true);
        qb.addCriterionEntity("c.provider", provider);
        return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
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

    @Override
    public void create(CustomFieldTemplate e, User creator, Provider provider) {
        super.create(e, creator, provider);
        customFieldsCache.addUpdateCustomFieldTemplate(e);
    }

    @Override
    public CustomFieldTemplate update(CustomFieldTemplate e, User updater) {
        CustomFieldTemplate eUpdated = super.update(e, updater);
        customFieldsCache.addUpdateCustomFieldTemplate(e);

        return eUpdated;
    }

    @Override
    public void remove(CustomFieldTemplate e) {
        super.remove(e);
        customFieldsCache.removeCustomFieldTemplate(e);
    }

    public List<CustomFieldTemplate> getCFTForCache() {
        return getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTForCache", CustomFieldTemplate.class).getResultList();
    }
}
