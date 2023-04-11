/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.action.admin.custom;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.SortOrder;

@Named

public class CustomEntityTemplateListBean extends CustomEntityTemplateBean {

    private static final long serialVersionUID = 7731570832396817056L;

    @Inject
    private CustomizedEntityService customizedEntityService;

    private LazyDataModelWSize<CustomizedEntity> customizedEntityDM = null;

    public LazyDataModelWSize<CustomizedEntity> getCustomizedEntities() {

        if (customizedEntityDM != null) {
            return customizedEntityDM;
        }

        customizedEntityDM = new LazyDataModelWSize<CustomizedEntity>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<CustomizedEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

                List<CustomizedEntity> entities = null;
                String query = (String) filters.get("entityName");
                boolean isCustomEntityOnly = filters.get("customEntity") != null && (boolean) filters.get("customEntity");
                String sortBy = sortOrder != null ? sortOrder.name() : null;

                entities = customizedEntityService.getCustomizedEntities(query, isCustomEntityOnly, false, false, sortField, sortBy);
                setRowCount(entities.size());

                return entities.subList(first, (first + pageSize) > entities.size() ? entities.size() : (first + pageSize));
            }
        };

        return customizedEntityDM;
    }
}