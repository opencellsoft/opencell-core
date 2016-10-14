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
package org.meveocrm.admin.action.reporting;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.meveo.model.dwh.BarChart;
import org.meveo.model.dwh.Chart;
import org.meveo.model.dwh.LineChart;
import org.meveo.model.dwh.PieChart;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.ChartModel;

@Named
@ViewScoped
public class ChartBean extends ChartEntityBean<Chart,ChartModel,ChartEntityModel<Chart, ChartModel>> {

	private static final long serialVersionUID = 2585685452044860823L;

	public ChartBean() {
		super(Chart.class);
	}

	public ChartBean(Class<Chart> clazz) {
		super(clazz);
	}

	public String getEditView(Chart chart) {

		if (chart instanceof BarChart) {
			return "/pages/reporting/dwh/barChartDetail.xhtml";
		}
		if (chart instanceof PieChart) {
			return "/pages/reporting/dwh/pieChartDetail.xhtml";
		}
		if (chart instanceof LineChart) {
			return "/pages/reporting/dwh/lineChartDetail.xhtml";
		}
		return "/pages/reporting/dwh/barChartDetail.xhtml";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}
	
	
	@Override
	public LazyDataModel<Chart> getLazyDataModel() {
		getFilters();
		if (filters.containsKey("user")) {
			filters.put("auditable.creator", filters.get("user"));
			filters.remove("user");
		}
		return super.getLazyDataModel();
	}
	

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
