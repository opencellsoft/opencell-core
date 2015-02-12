package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.BarChartService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * 
 * @author Luis Alfonso L. Mance
 * 
 */
@Named
@ConversationScoped
public class BarChartBean extends ChartEntityBean<BarChart> {

	private static final long serialVersionUID = 8644183603983960104L;

	@Inject
	private BarChartService barChartService;

	@Inject
	MeasuredValueService mvService;

	private BarChartEntityModel chartEntityModel;

	public BarChartBean() {
		super(BarChart.class);
	}

	@Override
	protected IPersistenceService<BarChart> getPersistenceService() {
		// TODO Auto-generated method stub
		return barChartService;
	}

	@Override
	protected String getListViewName() {
		return "charts";
	}

	public List<BarChartEntityModel> getChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

		List<BarChartEntityModel> chartModelList = new ArrayList<BarChartEntityModel>();
		List<BarChart> barChartList = barChartService.list();

		for (BarChart barChart : barChartList) {
			MeasurableQuantity mq = barChart.getMeasurableQuantity();
			List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
					fromDate.getTime(), toDate.getTime(), null, mq);

			CartesianChartModel chartModel = new CartesianChartModel();

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
			ChartSeries mvSeries = new ChartSeries();

			mvSeries.setLabel(sdf.format(fromDate.getTime()));

			if (mvs.size() > 0) {
				for (MeasuredValue measuredValue : mvs) {
					mvSeries.set(sdf.format(measuredValue.getDate()),
							measuredValue.getValue());
				}
				chartModel.addSeries(mvSeries);
			} else {
				log.info("No measured values found for : " + mq.getCode());
			}

			BarChartEntityModel chartEntityModel = new BarChartEntityModel();
			chartEntityModel.setBarChart(barChart);
			chartEntityModel.setModel(chartModel);
			chartModelList.add(chartEntityModel);

		}

		return chartModelList;
	}

	public BarChartEntityModel getChartEntityModel() {

		if (chartEntityModel == null) {
			chartEntityModel = new BarChartEntityModel();
		}
		if (getEntity() != null) {
			if (getEntity().getMeasurableQuantity() != null) {
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(Calendar.DAY_OF_MONTH, 1);
				Calendar toDate = Calendar.getInstance();
				toDate.setTime(fromDate.getTime());
				toDate.add(Calendar.MONTH, 1);

				MeasurableQuantity mq = getEntity().getMeasurableQuantity();
				List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
						fromDate.getTime(), toDate.getTime(), null, mq);

				CartesianChartModel chartModel = new CartesianChartModel();

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
				ChartSeries mvSeries = new ChartSeries();

				mvSeries.setLabel(sdf.format(fromDate.getTime()));

				if (mvs.size() > 0) {
					for (MeasuredValue measuredValue : mvs) {
						mvSeries.set(sdf.format(measuredValue.getDate()),
								measuredValue.getValue());
					}
					chartModel.addSeries(mvSeries);
				} else {
					log.info("No measured values found for : " + mq.getCode());
				}

				chartEntityModel.setModel(chartModel);
				chartEntityModel.setBarChart(getEntity());
			}
		}
		return chartEntityModel;
	}

	public void setChartEntityModel(BarChartEntityModel chartEntityModel) {
		this.chartEntityModel = chartEntityModel;
	}

	public BarChartEntityModel getModel(BarChartEntityModel bar) {

		MeasurableQuantity mq = bar.getBarChart().getMeasurableQuantity();
		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
				bar.getMinDate(), bar.getMaxDate(), null, mq);

		CartesianChartModel chartModel = new CartesianChartModel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(bar.getMinDate()));

		if (mvs.size() > 0) {
			for (MeasuredValue measuredValue : mvs) {
				mvSeries.set(sdf.format(measuredValue.getDate()),
						measuredValue.getValue());
			}
			chartModel.addSeries(mvSeries);
		} else {
			log.info("No measured values found for : " + mq.getCode());
		}

		BarChartEntityModel result = new BarChartEntityModel();
		result.setBarChart(bar.getBarChart());
		result.setModel(chartModel);

		return result;
	}

}
