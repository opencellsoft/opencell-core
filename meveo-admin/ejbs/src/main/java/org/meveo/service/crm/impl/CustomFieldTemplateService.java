package org.meveo.service.crm.impl;

import java.util.Arrays;
import java.util.Collection;
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

    /**
     * Find a list of custom field templates corresponding to a given entity
     * 
     * @param entity Entity that custom field templates apply to
     * @param provider Provider
     * @return A list of custom field templates
     */
    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByAppliesTo(ICustomFieldEntity entity, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", Arrays.asList("calendar"), provider);
        String appliesTo = calculateAppliesToValue(entity);
        qb.addCriterion("c.appliesTo", "=", appliesTo, true);

        return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Find a list of custom field templates corresponding to a given entity
     * 
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @param provider Provider
     * @return A list of custom field templates
     */
    @SuppressWarnings("unchecked")
    public List<CustomFieldTemplate> findByAppliesTo(String appliesTo, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldTemplate.class, "c", Arrays.asList("calendar"), provider);
        qb.addCriterion("c.appliesTo", "=", appliesTo, true);

        return (List<CustomFieldTemplate>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Find a specific custom field template by a code
     * 
     * @param code Custom field template code
     * @param entity Entity that custom field templates apply to
     * @param provider Provider
     * @return Custom field template
     */
    public CustomFieldTemplate findByCodeAndAppliesTo(String code, ICustomFieldEntity entity, Provider provider) {
        return findByCodeAndAppliesTo(code, calculateAppliesToValue(entity), provider);
    }

    /**
     * Find a specific custom field template by a code
     * 
     * @param code Custom field template code
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @param provider Provider
     * @return Custom field template
     */
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

    /**
     * Get a list of custom field templates for cache
     * 
     * @return A list of custom field templates
     */
    public List<CustomFieldTemplate> getCFTForCache() {
        return getEntityManager().createNamedQuery("CustomFieldTemplate.getCFTForCache", CustomFieldTemplate.class).getResultList();
    }

    /**
     * Calculate custom field template AppliesTo value for a given entity. AppliesTo consist of a prefix and optionally one or more entity fields. e.g. JOB_<jobTemplate>
     * 
     * @param entity Entity
     * @return A appliesTo value
     */
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

    /**
     * Check and create missing templates given a list of templates
     * 
     * @param entity Entity that custom field templates apply to
     * @param templates A list of templates to check
     * @param provider Provider
     * @return A complete list of templates for a given entity and provider
     */
    public List<CustomFieldTemplate> createMissingTemplates(ICustomFieldEntity entity, Collection<CustomFieldTemplate> templates, Provider provider) {
        return createMissingTemplates(calculateAppliesToValue(entity), templates, provider);
    }

    /**
     * Check and create missing templates given a list of templates
     * 
     * @param appliesTo Entity (CFT appliesTo code) that custom field templates apply to
     * @param templates A list of templates to check
     * @param provider Provider
     * @return A complete list of templates for a given entity and provider
     */
    public List<CustomFieldTemplate> createMissingTemplates(String appliesTo, Collection<CustomFieldTemplate> templates, Provider provider) {

        // Get templates corresponding to an entity type
        List<CustomFieldTemplate> allTemplates = findByAppliesTo(appliesTo, provider);

        if (templates != null) {
            for (CustomFieldTemplate cf : templates) {
                if (!allTemplates.contains(cf)) {
                    create(cf, getCurrentUser(), provider);
                    allTemplates.add(cf);
                }
            }
        }
        return allTemplates;
    }
}
