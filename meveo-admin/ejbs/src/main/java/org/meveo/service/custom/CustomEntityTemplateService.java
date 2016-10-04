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
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
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
    public void create(CustomEntityTemplate cet, User creator) throws BusinessException {
        super.create(cet, creator);
        customFieldsCache.addUpdateCustomEntityTemplate(cet);

        elasticClient.createCETMapping(cet);

        try {
            permissionService.createIfAbsent("modify", cet.getPermissionResourceName(), creator, paramBean.getProperty("role.modifyAllCE", "ModifyAllCE"));
            permissionService.createIfAbsent("read", cet.getPermissionResourceName(), creator, paramBean.getProperty("role.readAllCE", "ReadAllCE"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomEntityTemplate update(CustomEntityTemplate cet, User updater) throws BusinessException {
        CustomEntityTemplate cetUpdated = super.update(cet, updater);
        customFieldsCache.addUpdateCustomEntityTemplate(cet);

        try {
            permissionService.createIfAbsent("modify", cet.getPermissionResourceName(), updater, paramBean.getProperty("role.modifyAllCE", "ModifyAllCE"));
            permissionService.createIfAbsent("read", cet.getPermissionResourceName(), updater, paramBean.getProperty("role.readAllCE", "ReadAllCE"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return cetUpdated;
    }

    @Override
    public void remove(Long id, User currentUser) throws BusinessException {

        CustomEntityTemplate cet = findById(id);

        Map<String, CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo(), cet.getProvider());

        for (CustomFieldTemplate cft : fields.values()) {
            customFieldTemplateService.remove(cft.getId(), currentUser);
        }
        super.remove(id, currentUser);

        customFieldsCache.removeCustomEntityTemplate(cet);
    }

    @Override
    public List<CustomEntityTemplate> list(Provider provider, Boolean active) {

        boolean useCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCET", "true"));
        if (useCache && (active == null || active)) {

            List<CustomEntityTemplate> cets = new ArrayList<CustomEntityTemplate>();
            cets.addAll(customFieldsCache.getCustomEntityTemplates(provider));
            return cets;

        } else {
            return super.list(provider, active);
        }
    }

    public List<CustomEntityTemplate> listNoCache(Provider provider) {
        return super.list(provider, null);
    }

    @Override
    public List<CustomEntityTemplate> list(PaginationConfiguration config) {

        boolean useCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCET", "true"));
        if (useCache
                && (config.getFilters() == null || config.getFilters().isEmpty() || (config.getFilters().size() == 1 && config.getFilters().get("disabled") != null && !(boolean) config
                    .getFilters().get("disabled")))) {
            List<CustomEntityTemplate> cets = new ArrayList<CustomEntityTemplate>();
            cets.addAll(customFieldsCache.getCustomEntityTemplates(getCurrentProvider()));
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
}