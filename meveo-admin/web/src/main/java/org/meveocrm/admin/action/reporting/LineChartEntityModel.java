package org.meveocrm.admin.action.reporting;

import org.meveocrm.model.dwh.LineChart;
import org.primefaces.model.chart.CartesianChartModel;

public class LineChartEntityModel extends ChartEntityModel {

	private LineChart lineChart;
	private CartesianChartModel model;

	public LineChart getLineChart() {
		return lineChart;
	}

	public void setLineChart(LineChart lineChart) {
		this.lineChart = lineChart;
	}

	public CartesianChartModel getModel() {
		return model;
	}

	public void setModel(CartesianChartModel model) {
		this.model = model;
	}

}
