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

import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

@Named
@ViewScoped
public class SubscriptionChargeTemplateBean extends BaseBean<OneShotChargeTemplate> {
	private static final long serialVersionUID = 1L;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	
	public  SubscriptionChargeTemplateBean() {
		super(OneShotChargeTemplate.class);
	}
	
	@Override
	public void search() {
		log.debug("search");
		getFilters();
		if (!filters.containsKey("disabled")) {
			filters.put("disabled", false);
		}
		if (!filters.containsKey("oneShotChargeTemplateType")) {
			log.debug("put oneShotChargeTemplateType");
			filters.put("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.SUBSCRIPTION);
		}
		super.search();
	}

	@Override
	protected IPersistenceService<OneShotChargeTemplate> getPersistenceService() {
		return oneShotChargeTemplateService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
	public List<OneShotChargeTemplate> getSubscriptionCharges() {
		List<OneShotChargeTemplate> result = oneShotChargeTemplateService.getSubscriptionChargeTemplates();
		return result;
	}
	
	public List<OneShotChargeTemplate> getTerminationCharges() {
		List<OneShotChargeTemplate> result = oneShotChargeTemplateService.getTerminationChargeTemplates();
		return result;
	}

}
