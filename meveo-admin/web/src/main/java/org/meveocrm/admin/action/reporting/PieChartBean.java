package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.model.dwh.PieChart;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.meveocrm.services.dwh.PieChartService;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 * 
 * @author Luis Alfonso L. Mance
 * 
 */
@Named
@ConversationScoped
public class PieChartBean extends ChartEntityBean<PieChart> {

	private static final long serialVersionUID = 3731010636973175230L;

	@Inject
	private PieChartService pieChartService;

	@Inject
	MeasuredValueService mvService;

	private PieChartEntityModel chartEntityModel;

	public PieChartBean() {
		super(PieChart.class);
	}

	@Override
	protected IPersistenceService<PieChart> getPersistenceService() {
		// TODO Auto-generated method stub
		return pieChartService;
	}

	@Override
	protected String getListViewName() {
		return "charts";
	}

	public List<PieChartEntityModel> getChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

		List<PieChartEntityModel> chartModelList = new ArrayList<PieChartEntityModel>();
		List<PieChart> pieChartList = pieChartService.list();

		for (PieChart pieChart : pieChartList) {

			MeasurableQuantity mq = pieChart.getMeasurableQuantity();
			List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
					fromDate.getTime(), toDate.getTime(), null, mq);

			PieChartModel chartModel = new PieChartModel();

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
			ChartSeries mvSeries = new ChartSeries();

			mvSeries.setLabel(sdf.format(fromDate.getTime()));

			if (mvs.size() > 0) {
				for (MeasuredValue measuredValue : mvs) {
					chartModel.set(sdf.format(measuredValue.getDate()),
							measuredValue.getValue());
				}
			} else {
				log.info("No measured values found for : " + mq.getCode());
			}

			PieChartEntityModel chartEntityModel = new PieChartEntityModel();
			chartEntityModel.setPieChart(pieChart);
			chartEntityModel.setModel(chartModel);

			chartModelList.add(chartEntityModel);

		}

		return chartModelList;
	}

	public PieChartEntityModel getChartEntityModel() {

		if (chartEntityModel == null) {
			chartEntityModel = new PieChartEntityModel();
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

				PieChartModel chartModel = new PieChartModel();

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
				ChartSeries mvSeries = new ChartSeries();

				mvSeries.setLabel(sdf.format(fromDate.getTime()));

				if (mvs.size() > 0) {
					for (MeasuredValue measuredValue : mvs) {
						chartModel.set(sdf.format(measuredValue.getDate()),
								measuredValue.getValue());
					}
				} else {
					log.info("No measured values found for : " + mq.getCode());
				}

				chartEntityModel.setModel(chartModel);
				chartEntityModel.setPieChart(getEntity());
			}
		}
		return chartEntityModel;
	}

	public PieChartEntityModel getModel(PieChartEntityModel pie) {

		MeasurableQuantity mq = pie.getPieChart().getMeasurableQuantity();
		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
				pie.getMinDate(), pie.getMaxDate(), null, mq);

		PieChartModel chartModel = new PieChartModel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(pie.getMinDate()));

		if (mvs.size() > 0) {
			for (MeasuredValue measuredValue : mvs) {
				chartModel.set(sdf.format(measuredValue.getDate()),
						measuredValue.getValue());
			}
		} else {
			log.info("No measured values found for : " + mq.getCode());
		}

		PieChartEntityModel result = new PieChartEntityModel();
		result.setPieChart(pie.getPieChart());
		result.setModel(chartModel);

		return result;
	}
}
