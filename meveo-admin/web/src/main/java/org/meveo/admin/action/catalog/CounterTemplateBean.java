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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

@Named
@ViewScoped
public class CounterTemplateBean extends BaseBean<CounterTemplate> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PricePlanMatrix} service. Extends
	 * {@link PersistenceService}.
	 */
	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CounterTemplateBean() {
		super(CounterTemplate.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected IPersistenceService<CounterTemplate> getPersistenceService() {
		return counterTemplateService;
	}


	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	/**
     * DataModel for primefaces lazy loading datatable component.
     * 
     * @return LazyDataModel implementation.
     */
    public LazyDataModel<CounterTemplate> getLazyDataModel(CounterTypeEnum counterType) {
        filters.put("counterType", counterType);
        return getLazyDataModel(filters, false);
    }	
}