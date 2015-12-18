package org.meveocrm.admin.action.reporting;

import org.meveocrm.model.dwh.LineChart;
import org.primefaces.model.chart.LineChartModel;

public class LineChartEntityModel extends ChartEntityModel {

	private LineChart lineChart;
	private LineChartModel model;

	public LineChart getLineChart() {
		return lineChart;
	}

	public void setLineChart(LineChart lineChart) {
		this.lineChart = lineChart;
	}

	public LineChartModel getModel() {
		return model;
	}

	public void setModel(LineChartModel model) {
		this.model = model;
	}

}
