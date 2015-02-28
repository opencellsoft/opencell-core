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
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Standard backing bean for {@link BillingCycle} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class CounterPeriodBean extends BaseBean<CounterPeriod> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link BillingCycle} service. Extends {@link PersistenceService}
	 * .
	 */
	@Inject
	private CounterPeriodService counterPeriodService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CounterPeriodBean() {
		super(CounterPeriod.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<CounterPeriod> getPersistenceService() {
		return counterPeriodService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
	
	public LazyDataModel<CounterPeriod> getCounterPeriods(CounterInstance counterInstance) {
		  if (counterInstance != null) {
		   filters.put("counterInstance", counterInstance);
		   return getLazyDataModel();
		  }

		  return new LazyDataModel<CounterPeriod>() {  
		   private static final long serialVersionUID = 1L;
		   @Override
		   public List<CounterPeriod> load(int first, int pageSize, String sortField,
					SortOrder sortOrder, Map<String, String> loadingFilters) {
		    return new ArrayList<CounterPeriod>();
		   }
		  };
		 }
	
	
	
	 
	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
