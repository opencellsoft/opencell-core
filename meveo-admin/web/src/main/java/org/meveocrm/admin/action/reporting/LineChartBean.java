package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.LineChartService;
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
public class LineChartBean extends ChartEntityBean<LineChart> {

	private static final long serialVersionUID = -5171375359771241684L;

	@Inject
	private LineChartService lineChartService;

	@Inject
	MeasuredValueService mvService;

	private LineChartEntityModel chartEntityModel;

	public LineChartBean() {
		super(LineChart.class);
	}

	@Override
	protected IPersistenceService<LineChart> getPersistenceService() {
		// TODO Auto-generated method stub
		return lineChartService;
	}

	@Override
	protected String getListViewName() {
		return "charts";
	}

	public List<LineChartEntityModel> getChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

		List<LineChartEntityModel> chartModelList = new ArrayList<LineChartEntityModel>();
		List<LineChart> lineChartList = lineChartService.list();

		for (LineChart lineChart : lineChartList) {

			MeasurableQuantity mq = lineChart.getMeasurableQuantity();
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

			LineChartEntityModel chartEntityModel = new LineChartEntityModel();
			chartEntityModel.setLineChart(lineChart);
			chartEntityModel.setModel(chartModel);

			chartModelList.add(chartEntityModel);

		}

		return chartModelList;
	}

	public LineChartEntityModel getChartEntityModel() {

		if (chartEntityModel == null) {
			chartEntityModel = new LineChartEntityModel();
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
				chartEntityModel.setLineChart(getEntity());
			}
		}
		return chartEntityModel;
	}

	public LineChartEntityModel getModel(LineChartEntityModel line) {

		MeasurableQuantity mq = line.getLineChart().getMeasurableQuantity();
		Calendar cal = Calendar.getInstance();
		cal.setTime(line.getMaxDate());
		cal.add(Calendar.DATE, 1);
		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
				line.getMinDate(), cal.getTime(), null, mq);

		CartesianChartModel chartModel = new CartesianChartModel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(line.getMinDate()));

		if (mvs.size() > 0) {
			for (MeasuredValue measuredValue : mvs) {
				mvSeries.set(sdf.format(measuredValue.getDate()),
						measuredValue.getValue());
			}
			chartModel.addSeries(mvSeries);
		} else {
			log.info("No measured values found for : " + mq.getCode());
		}

		LineChartEntityModel result = new LineChartEntityModel();
		result.setLineChart(line.getLineChart());
		result.setModel(chartModel);

		return result;
	}
}
