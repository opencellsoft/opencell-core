/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.custom;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Stateless
public class CustomEntityTemplateService extends BusinessService<CustomEntityTemplate> {

    @Inject
    CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomFieldsCacheContainerProvider customFieldsCache;

    @Override
    public void create(CustomEntityTemplate e, User creator, Provider provider) {
        super.create(e, creator, provider);
        customFieldsCache.addUpdateCustomEntityTemplate(e);
    }

    @Override
    public CustomEntityTemplate update(CustomEntityTemplate e, User updater) {
        CustomEntityTemplate eUpdated = super.update(e, updater);
        customFieldsCache.addUpdateCustomEntityTemplate(e);

        return eUpdated;
    }

    @Override
    public void remove(Long id) {

        CustomEntityTemplate cet = findById(id);

        List<CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesTo(cet.getCftPrefix(), cet.getProvider());

        for (CustomFieldTemplate cft : fields) {
            customFieldTemplateService.remove(cft.getId());
        }
        super.remove(id);

        customFieldsCache.removeCustomEntityTemplate(cet);
    }

    @Override
    public List<CustomEntityTemplate> list(Provider provider, Boolean active) {

        boolean useCache = true;
        if (useCache && (active == null || active)) {
            return customFieldsCache.getCustomEntityTemlates(provider);
        } else {
            return super.list(provider, active);
        }
    }

    @Override
    public List<CustomEntityTemplate> list(PaginationConfiguration config) {

        boolean useCache = true;
        if (useCache) {
            return customFieldsCache.getCustomEntityTemlates(getCurrentProvider());
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