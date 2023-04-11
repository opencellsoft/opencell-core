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
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Standard backing bean for {@link BillingCycle} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named

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

        return new LazyDataModelWSize<CounterPeriod>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<CounterPeriod> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {
                return new ArrayList<CounterPeriod>();
            }
        };
    }	
}