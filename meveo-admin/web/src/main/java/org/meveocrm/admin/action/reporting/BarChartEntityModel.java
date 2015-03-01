package org.meveocrm.admin.action.reporting;

import org.meveocrm.model.dwh.BarChart;
import org.primefaces.model.chart.CartesianChartModel;

public class BarChartEntityModel extends ChartEntityModel {

	private BarChart barChart;
	private CartesianChartModel model;

	public BarChart getBarChart() {
		return barChart;
	}

	public void setBarChart(BarChart barChart) {
		this.barChart = barChart;
	}

	public CartesianChartModel getModel() {
		return model;
	}

	public void setModel(CartesianChartModel model) {
		this.model = model;
	}

}
