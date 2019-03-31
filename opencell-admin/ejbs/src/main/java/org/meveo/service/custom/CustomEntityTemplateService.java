/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.custom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.MapUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.index.ElasticClient;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
public class CustomEntityTemplateService extends BusinessService<CustomEntityTemplate> {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private CustomFieldsCacheContainerProvider customFieldsCache;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    private CustomTableCreatorService customTableCreatorService;

    private static boolean useCETCache = true;

    @PostConstruct
    private void init() {
        useCETCache = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("cache.cacheCET", "true"));
    }

    @Override
    public void create(CustomEntityTemplate cet) throws BusinessException {

        ParamBean paramBean = paramBeanFactory.getInstance();
        super.create(cet);
        customFieldsCache.addUpdateCustomEntityTemplate(cet);

        if (cet.isStoreAsTable()) {
            customTableCreatorService.createTable(cet.getDbTablename());
        }

        elasticClient.createCETMapping(cet);

        try {
            permissionService.createIfAbsent(cet.getModifyPermission(), paramBean.getProperty("role.modifyAllCE", "ModifyAllCE"));
            permissionService.createIfAbsent(cet.getReadPermission(), paramBean.getProperty("role.readAllCE", "ReadAllCE"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomEntityTemplate update(CustomEntityTemplate cet) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        CustomEntityTemplate cetUpdated = super.update(cet);

        customFieldsCache.addUpdateCustomEntityTemplate(cet);

        try {
            permissionService.createIfAbsent(cet.getModifyPermission(), paramBean.getProperty("role.modifyAllCE", "ModifyAllCE"));
            permissionService.createIfAbsent(cet.getReadPermission(), paramBean.getProperty("role.readAllCE", "ReadAllCE"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return cetUpdated;
    }

    @Override
    public void remove(CustomEntityTemplate cet) throws BusinessException {

        Map<String, CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());

        for (CustomFieldTemplate cft : fields.values()) {
            customFieldTemplateService.remove(cft.getId());
        }
        super.remove(cet);

        if (cet.isStoreAsTable()) {
            customTableCreatorService.removeTable(cet.getDbTablename());
        }

        customFieldsCache.removeCustomEntityTemplate(cet);

        // Remove from ES
        elasticClient.removeCETMapping(cet);

    }

    /**
     * List custom entity templates, optionally filtering by an active status. Custom entity templates will be looked up in cache or retrieved from DB.
     * 
     * @param active Custom entity template's status. Or any if null
     * @return A list of custom entity templates
     */
    @Override
    public List<CustomEntityTemplate> list(Boolean active) {

        if (useCETCache && (active == null || active)) {

            List<CustomEntityTemplate> cets = new ArrayList<CustomEntityTemplate>();
            cets.addAll(customFieldsCache.getCustomEntityTemplates());

            // Populate cache if record is not found in cache
            if (cets.isEmpty()) {
                cets = super.list(active);
                if (cets != null) {
                    cets.forEach((cet) -> customFieldsCache.addUpdateCustomEntityTemplate(cet));
                }
            }

            return cets;

        } else {
            return super.list(active);
        }
    }

    public List<CustomEntityTemplate> listNoCache() {
        return super.list((Boolean) null);
    }

    /**
     * Search a list of custom entity templates by code and storeAsTable
     * 
     * @param code
     * @param storeAsTable
     * @return A list of custom entity templates
     */
    @SuppressWarnings("unchecked")
    public List<CustomEntityTemplate> search(String code, boolean storeAsTable) {
        QueryBuilder queryBuilder = new QueryBuilder(CustomEntityTemplate.class, "cet", null);

        if (!StringUtils.isBlank(code)) {
            queryBuilder.like("code", code, QueryLikeStyleEnum.MATCH_ANYWHERE, false);
        }
        queryBuilder.addBooleanCriterion("storeAsTable", storeAsTable);

        return queryBuilder.getQuery(getEntityManager()).getResultList();
    }

    @Override
    public List<CustomEntityTemplate> list(PaginationConfiguration config) {

        if (useCETCache && (config.getFilters() == null || config.getFilters().isEmpty()
                || (config.getFilters().size() == 1 && config.getFilters().get("disabled") != null && !(boolean) config.getFilters().get("disabled")))) {
            List<CustomEntityTemplate> cets = new ArrayList<CustomEntityTemplate>();
            cets.addAll(customFieldsCache.getCustomEntityTemplates());

            // Populate cache if record is not found in cache
            if (cets.isEmpty()) {
                cets = super.list(config);
                if (cets != null) {
                    cets.forEach((cet) -> customFieldsCache.addUpdateCustomEntityTemplate(cet));
                }
            }

            // Order the list
            if (config.getSortField() != null) {
                Comparator<CustomEntityTemplate> comparator = null;
                if ("description".equals(config.getSortField())) {
                    comparator = Comparator.comparing(CustomEntityTemplate::getDescription);
                } else if ("code".equals(config.getSortField())) {
                    comparator = Comparator.comparing(CustomEntityTemplate::getCode);
                } else if ("name".equals(config.getSortField())) {
                    comparator = Comparator.comparing(CustomEntityTemplate::getName);
                }
                if (!config.isAscendingSorting()) {
                    comparator = comparator.reversed();
                }
                cets.sort(comparator);
            }
            return cets;

        } else {
            return super.list(config);
        }
    }

    /**
     * Get a list of custom entity templates for cache
     * 
     * @return A list of custom entity templates
     */
    public List<CustomEntityTemplate> getCETForCache() {
        return getEntityManager().createNamedQuery("CustomEntityTemplate.getCETForCache", CustomEntityTemplate.class).getResultList();
    }

    /**
     * A generic method that returns a filtered list of ICustomFieldEntity given an entity class and code.
     * 
     * @param entityClass - class of an entity. eg. org.meveo.catalog.OfferTemplate
     * @param entityCode - code of entity
     * @return customer field entity
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ICustomFieldEntity findByClassAndCode(Class entityClass, String entityCode) {
        ICustomFieldEntity result = null;
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        queryBuilder.addCriterion("code", "=", entityCode, true);
        List<ICustomFieldEntity> entities = (List<ICustomFieldEntity>) queryBuilder.getQuery(getEntityManager()).getResultList();
        if (entities != null && !entities.isEmpty()) {
            result = entities.get(0);
        }

        return result;
    }

    @Override
    public CustomEntityTemplate findByCode(String code) {

        if (useCETCache) {

            CustomEntityTemplate cet = customFieldsCache.getCustomEntityTemplate(code);

            // Populate cache if record is not found in cache
            if (cet == null) {
                cet = super.findByCode(code);
                if (cet != null && cet.isActive()) {
                    customFieldsCache.addUpdateCustomEntityTemplate(cet);
                }
            }

            return cet;

        } else {
            return super.findByCode(code);
        }
    }

    /**
     * Get custom entity template by code without using cache (straight from DB)
     * 
     * @param code Custom entity code
     * @return Custom entity template
     */
    public CustomEntityTemplate findByCodeNoCache(String code) {
        return super.findByCode(code);
    }

    /**
     * Get a list of custom entity templates that use custom tables as implementation
     * 
     * @return A list of custom entity templates
     */
    @SuppressWarnings("unchecked")
    public List<CustomEntityTemplate> listCustomTableTemplates() {

        if (useCETCache) {
            List<CustomEntityTemplate> cets = new ArrayList<>();
            for (CustomEntityTemplate customEntityTemplate : customFieldsCache.getCustomEntityTemplates()) {
                if (customEntityTemplate.isStoreAsTable()) {
                    cets.add(customEntityTemplate);
                }
            }
            return cets;

        } else {
            return super.list(new PaginationConfiguration(MapUtils.putAll(new HashMap<String, Object>(), new Object[] { "storeAsTable", true })));
        }
    }

    /**
     * Find a custom entity template that uses a given custom table as implementation
     * 
     * @param dbTablename Database table name
     * @return A custom entity template
     */
    public CustomEntityTemplate findByDbTablename(String dbTablename) {

        List<CustomEntityTemplate> cets = listCustomTableTemplates();

        for (CustomEntityTemplate cet : cets) {
            if (cet.getDbTablename().equalsIgnoreCase(dbTablename)) {
                return cet;
            }
        }
        return null;
    }

    /**
     * Find a custom entity template that uses a given custom table as implementation
     * 
     * @param codeOrDbTablename Custom entity code or a corresponding database table name
     * @return A custom entity template
     */
    public CustomEntityTemplate findByCodeOrDbTablename(String codeOrDbTablename) {

        CustomEntityTemplate cet = findByCode(codeOrDbTablename);
        if (cet != null) {
            return cet;
        }
        return findByDbTablename(codeOrDbTablename);
    }

    @Override
    public CustomEntityTemplate disable(CustomEntityTemplate cet) throws BusinessException {

        cet = super.disable(cet);
        customFieldsCache.removeCustomEntityTemplate(cet);

        return cet;
    }

    @Override
    public CustomEntityTemplate enable(CustomEntityTemplate cet) throws BusinessException {

        cet = super.enable(cet);
        customFieldsCache.addUpdateCustomEntityTemplate(cet);
        return cet;
    }
}