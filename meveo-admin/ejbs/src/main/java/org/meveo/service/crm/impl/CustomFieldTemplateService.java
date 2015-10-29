package org.meveo.service.crm.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;

@Stateless
public class CustomFieldTemplateService extends BusinessService<CustomFieldTemplate> {

    @Inject
    private CustomFieldsCacheContainerProvider customFieldsCache;

    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByAppliesTo(ICustomFieldEntity entity, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", Arrays.asList("calendar"), provider);
        String appliesTo = calculateAppliesToValue(entity);
        qb.addCriterion("c.appliesTo", "=", appliesTo, true);

        return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
    }

    public CustomFieldTemplate findByCodeAndAppliesTo(String code, ICustomFieldEntity entity, Provider provider) {
        return findByCodeAndAppliesTo(code, calculateAppliesToValue(entity), provider);
    }

    public CustomFieldTemplate findByCodeAndAppliesTo(String code, String appliesTo, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", null, provider);
        qb.addCriterion("code", "=", code, true);
        qb.addCriterion("appliesTo", "=", appliesTo, true);
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

    public String calculateAppliesToValue(ICustomFieldEntity entity) {
        CustomFieldEntity cfeAnnotation = entity.getClass().getAnnotation(CustomFieldEntity.class);

        String appliesTo = cfeAnnotation.cftCodePrefix();
        if (cfeAnnotation.cftCodeFields().length > 0) {
            for (String fieldName : cfeAnnotation.cftCodeFields()) {
                try {
                    appliesTo = appliesTo + "_" + FieldUtils.getField(entity.getClass(), fieldName, true).get(entity);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Unable to access field {}.{}", entity.getClass().getSimpleName(), fieldName);
                    throw new RuntimeException("Unable to access field " + entity.getClass().getSimpleName() + "." + fieldName);
                }
            }
        }
        return appliesTo;
    }
}
