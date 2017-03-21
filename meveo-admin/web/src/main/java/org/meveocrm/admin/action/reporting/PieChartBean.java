package org.meveocrm.admin.action.reporting;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.meveo.model.dwh.PieChart;
import org.primefaces.model.chart.PieChartModel;

@Named
@ViewScoped
public class PieChartBean extends ChartEntityBean<PieChart,PieChartModel,PieChartEntityModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3731010636973175230L;

	public PieChartBean() {
		super(PieChart.class);
	}

	public PieChartBean(Class<PieChart> clazz) {
		super(clazz);
	}

}
