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
package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.component.datatable.DataTable;

@Named
@ViewScoped
public class TerminationChargeTemplateBean extends BaseBean<OneShotChargeTemplate> {
	private static final long serialVersionUID = 1L;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	
	public TerminationChargeTemplateBean() {
		super(OneShotChargeTemplate.class);
	}
	
	@Override
	public DataTable search() {
		log.debug("search");
		getFilters();
		if (!filters.containsKey("disabled")) {
			filters.put("disabled", false);
		}
		if (!filters.containsKey("oneShotChargeTemplateType")) {
			log.debug("put oneShotChargeTemplateType");
			filters.put("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.TERMINATION);
		}
		return super.search();
	}

	@Override
	protected IPersistenceService<OneShotChargeTemplate> getPersistenceService() {
		return oneShotChargeTemplateService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
}
