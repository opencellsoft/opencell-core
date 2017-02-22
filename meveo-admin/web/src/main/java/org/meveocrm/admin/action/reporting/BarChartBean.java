package org.meveocrm.admin.action.reporting;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.meveo.model.dwh.BarChart;
import org.primefaces.model.chart.BarChartModel;


@Named
@ViewScoped
public class BarChartBean extends ChartEntityBean<BarChart,BarChartModel,BarChartEntityModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8644183603983960104L;

	public BarChartBean() {
		super(BarChart.class);
	}

	public BarChartBean(Class<BarChart> clazz) {
		super(clazz);
	}
}
