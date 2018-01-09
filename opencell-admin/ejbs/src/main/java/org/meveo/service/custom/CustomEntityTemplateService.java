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
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.index.ElasticClient;

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

    private ParamBean paramBean = ParamBean.getInstance();

    @Override
    public void create(CustomEntityTemplate cet) throws BusinessException {
        super.create(cet);
        customFieldsCache.addUpdateCustomEntityTemplate(cet);

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
    public void remove(Long id) throws BusinessException {

        CustomEntityTemplate cet = findById(id);

        Map<String, CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());

        for (CustomFieldTemplate cft : fields.values()) {
            customFieldTemplateService.remove(cft.getId());
        }
        super.remove(id);

        customFieldsCache.removeCustomEntityTemplate(cet);
    }

    @Override
    public List<CustomEntityTemplate> list(Boolean active) {

        boolean useCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCET", "true"));
        if (useCache && (active == null || active)) {

            List<CustomEntityTemplate> cets = new ArrayList<CustomEntityTemplate>();
            cets.addAll(customFieldsCache.getCustomEntityTemplates());
            return cets;

        } else {
            return super.list(active);
        }
    }

    public List<CustomEntityTemplate> listNoCache() {
        return super.list((Boolean)null);
    }

    @Override
    public List<CustomEntityTemplate> list(PaginationConfiguration config) {

        boolean useCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCET", "true"));
        if (useCache
                && (config.getFilters() == null || config.getFilters().isEmpty() || (config.getFilters().size() == 1 && config.getFilters().get("disabled") != null && !(boolean) config
                    .getFilters().get("disabled")))) {
            List<CustomEntityTemplate> cets = new ArrayList<CustomEntityTemplate>();
            cets.addAll(customFieldsCache.getCustomEntityTemplates());
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
	 * A generic method that returns a filtered list of ICustomFieldEntity given
	 * an entity class and code.
	 * 
	 * @param entityClass
	 *            - class of an entity. eg. org.meveo.catalog.OfferTemplate
	 * @param entityCode
	 *            - code of entity
	 * @return customer field entity
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ICustomFieldEntity findByClassAndCode(Class entityClass, String entityCode) {
		ICustomFieldEntity result = null;
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
		queryBuilder.addCriterion("code", "=", entityCode, true);
		List<ICustomFieldEntity> entities = (List<ICustomFieldEntity>) queryBuilder.getQuery(getEntityManager())
				.getResultList();
		if (entities != null && !entities.isEmpty()) {
			result = entities.get(0);
		}

		return result;
	}
}