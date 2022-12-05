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

package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;

import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.PersistenceService;

@Named
@ConversationScoped
public class ProductAndBundleTemplateListBean extends ProductTemplateListBean {

    private static final long serialVersionUID = 7690305402189246824L;

    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        // Show product and bundle templates
        @SuppressWarnings("rawtypes")
        List<Class> types = new ArrayList<>();
        types.add(ProductTemplate.class);
        types.add(BundleTemplate.class);
        searchCriteria.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, types);

        return super.supplementSearchCriteria(searchCriteria);
    }
}