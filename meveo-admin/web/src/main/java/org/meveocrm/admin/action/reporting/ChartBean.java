package org.meveocrm.admin.action.reporting;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.ChartService;

import org.meveocrm.services.dwh.MeasuredValueService;
import org.primefaces.component.chart.bar.BarChart;
import org.primefaces.model.chart.ChartSeries;

@Named
@ConversationScoped
public class ChartBean extends BaseBean<Chart> {

	private static final long serialVersionUID = 2585685452044860823L;

	@Inject
	ChartService chartService;

	@Inject
	MeasuredValueService mvService;

	private BarChart barModel;

	public ChartBean() {
		super(Chart.class);
	}

	protected IPersistenceService<Chart> getPersistenceService() {
		return chartService;
	}

	protected String getDefaultViewName() {
		return "charts";
	}

	@Override
	protected String getListViewName() {
		return "charts";
	}

	public BarChart getBarModel() {

		ChartSeries barChartSeries = new ChartSeries();
		barChartSeries.setLabel("Measured Values");

		List<MeasuredValue> mvList = mvService.getByDateAndPeriod("", null,
				null, null, getEntity().getMeasurableQuantity());

		for (MeasuredValue measuredValue : mvList) {
			barChartSeries.set(measuredValue.getDate(),
					measuredValue.getValue());
		}

		return barModel;
	}

	public void setBarModel(BarChart barModel) {
		this.barModel = barModel;
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
