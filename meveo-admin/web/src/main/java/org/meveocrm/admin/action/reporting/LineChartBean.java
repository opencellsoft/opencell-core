package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.LineChartService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * 
 * @author Luis Alfonso L. Mance
 * 
 */
@Named
@ViewScoped
public class LineChartBean extends ChartEntityBean<LineChart> {

	private static final long serialVersionUID = -5171375359771241684L;

	@Inject
	private LineChartService lineChartService;

	@Inject
	MeasuredValueService mvService;

	private LineChartEntityModel chartEntityModel;

	private List<LineChartEntityModel> lineChartEntityModels = new ArrayList<LineChartEntityModel>();

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

	public void initChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

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
			boolean isAdmin = lineChart.getAuditable().getCreator()
					.hasRole("administrateur");
			boolean equalUser = lineChart.getAuditable().getCreator().getId() == getCurrentUser()
					.getId();
			boolean sameRoleWithChart = lineChart.getRole() != null ? getCurrentUser()
					.hasRole(lineChart.getRole().getDescription()) : false;
			lineChart.setVisible(isAdmin || equalUser || sameRoleWithChart);
			chartEntityModel.setLineChart(lineChart);
			chartEntityModel.setModel(chartModel);

			lineChartEntityModels.add(chartEntityModel);

		}

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

	public void setModel(Integer index) {

		LineChartEntityModel curr = lineChartEntityModels.get(index);
		MeasurableQuantity mq = curr.getLineChart().getMeasurableQuantity();
		if (!curr.getMinDate().before(curr.getMaxDate())) {
			curr.setMaxDate(curr.getMinDate());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(curr.getMaxDate());
		cal.add(Calendar.DATE, 1);
		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
				curr.getMinDate(), cal.getTime(), null, mq);

		CartesianChartModel chartModel = new CartesianChartModel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(curr.getMinDate()));

		if (mvs.size() > 0) {
			for (MeasuredValue measuredValue : mvs) {
				mvSeries.set(sdf.format(measuredValue.getDate()),
						measuredValue.getValue());
			}
			chartModel.addSeries(mvSeries);
		} else {
			log.info("No measured values found for : " + mq.getCode());
		}

		curr.setLineChart(curr.getLineChart());
		curr.setModel(chartModel);

	}

	public List<LineChartEntityModel> getLineChartEntityModels() {
		if (lineChartEntityModels.size() <= 0) {
			initChartModelList();
		}
		return lineChartEntityModels;
	}

	public void setLineChartEntityModels(
			List<LineChartEntityModel> lineChartEntityModels) {
		this.lineChartEntityModels = lineChartEntityModels;
	}

}
