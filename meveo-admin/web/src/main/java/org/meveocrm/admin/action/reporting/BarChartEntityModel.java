package org.meveocrm.admin.action.reporting;

import org.meveocrm.model.dwh.BarChart;
import org.primefaces.model.chart.BarChartModel;

public class BarChartEntityModel extends ChartEntityModel {

	private BarChart barChart;
	private BarChartModel model;

	public BarChart getBarChart() {
		return barChart;
	}

	public void setBarChart(BarChart barChart) {
		this.barChart = barChart;
	}

	public BarChartModel getModel() {
		return model;
	}

	public void setModel(BarChartModel model) {
		this.model = model;
	}

}
