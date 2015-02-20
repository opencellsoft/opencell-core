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
package org.meveocrm.admin.action.reporting;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.PieChart;
import org.meveocrm.services.dwh.ChartService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class ChartBean extends BaseBean<Chart> {

	private static final long serialVersionUID = 2585685452044860823L;

	@Inject
	ChartService chartService;

	public ChartBean() {
		super(Chart.class);

	}

	protected IPersistenceService<Chart> getPersistenceService() {
		return chartService;
	}
	
	@Override
	protected String getListViewName() {
		return "charts";
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
		return "/pages/reporting/dwh/chartDetail.xhtml";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
