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

package org.meveo.admin.action;

import java.io.Serializable;
import java.util.Map;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.meveo.admin.action.admin.custom.CustomFieldDataEntryBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tony Alejandro on 03/06/2016.
 */
@Named
@ViewScoped
public class FilterCustomFieldSearchBean implements Serializable {

	private static final long serialVersionUID = 4300150745614341095L;

	@Inject
	private CustomFieldDataEntryBean customFieldDataEntryBean;
	
	@Inject
    protected Messages messages;
	
	private static final Logger log = LoggerFactory.getLogger(FilterCustomFieldSearchBean.class);
	
    public void buildFilterParameters(Map<String, Object> filters) {
        if (filters != null && filters.containsKey("$FILTER")) {
            Filter entity = (Filter)filters.get("$FILTER");
            try {
                Map<CustomFieldTemplate, Object> parameterMap = customFieldDataEntryBean.loadCustomFieldsFromGUI(entity);
                filters.put("$FILTER_PARAMETERS", parameterMap);
            } catch (BusinessException e) {
                log.error("Failed to load search parameters from custom fields.", e);
                messages.error(e.getMessage());
            }
        }
    }

    public void saveOrUpdateFilter(Filter filter) throws BusinessException {
        boolean isNew = filter.isTransient();
        customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) filter, isNew);
    }
}
