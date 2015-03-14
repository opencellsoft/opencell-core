package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.BarChartService;
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
public class BarChartBean extends ChartEntityBean<BarChart> {

	private static final long serialVersionUID = 8644183603983960104L;

	@Inject
	private BarChartService barChartService;

	@Inject
	MeasuredValueService mvService;

	private BarChartEntityModel chartEntityModel;

	private List<BarChartEntityModel> barChartEntityModels = new ArrayList<BarChartEntityModel>();

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

	public List<BarChartEntityModel> initChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

		barChartEntityModels = new ArrayList<BarChartEntityModel>();
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
			boolean isAdmin = barChart.getAuditable().getCreator()
					.hasRole("administrateur");
			boolean equalUser = barChart.getAuditable().getCreator().getId() == getCurrentUser()
					.getId();
			boolean sameRoleWithChart = barChart.getRole() != null ? getCurrentUser()
					.hasRole(barChart.getRole().getDescription()) : false;
					barChart.setVisible(isAdmin || equalUser || sameRoleWithChart);
			chartEntityModel.setBarChart(barChart);
			chartEntityModel.setModel(chartModel);
			barChartEntityModels.add(chartEntityModel);

		}

		return barChartEntityModels;
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

	public void setModel(Integer modelIndex) {

		BarChartEntityModel curr = barChartEntityModels.get(modelIndex);
		MeasurableQuantity mq = curr.getBarChart().getMeasurableQuantity();
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

		curr.setBarChart(curr.getBarChart());
		curr.setModel(chartModel);
	}

	public List<BarChartEntityModel> getBarChartEntityModels() {
		if (barChartEntityModels.size() <= 0) {
			initChartModelList();
		}
		return barChartEntityModels;
	}

	public void setBarChartEntityModels(
			List<BarChartEntityModel> barChartEntityModels) {
		this.barChartEntityModels = barChartEntityModels;
	}

}
