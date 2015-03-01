package org.meveocrm.admin.action.reporting;

import org.meveocrm.model.dwh.PieChart;
import org.primefaces.model.chart.PieChartModel;

public class PieChartEntityModel extends ChartEntityModel {

	private PieChart pieChart;
	private PieChartModel model;

	public PieChartModel getModel() {
		return model;
	}

	public void setModel(PieChartModel model) {
		this.model = model;
	}

	public PieChart getPieChart() {
		return pieChart;
	}

	public void setPieChart(PieChart pieChart) {
		this.pieChart = pieChart;
	}

}
