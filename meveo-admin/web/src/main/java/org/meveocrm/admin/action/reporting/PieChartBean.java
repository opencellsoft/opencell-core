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

	private List<PieChartEntityModel> pieChartEntityModels = new ArrayList<PieChartEntityModel>();

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

	public void initChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

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
			boolean isAdmin = pieChart.getAuditable().getCreator()
					.hasRole("administrateur");
			boolean equalUser = pieChart.getAuditable().getCreator().getId() == getCurrentUser()
					.getId();
			boolean sameRoleWithChart = pieChart.getRole() != null ? getCurrentUser()
					.hasRole(pieChart.getRole().getDescription()) : false;
					pieChart.setVisible(isAdmin || equalUser || sameRoleWithChart);
			chartEntityModel.setPieChart(pieChart);
			chartEntityModel.setModel(chartModel);

			pieChartEntityModels.add(chartEntityModel);

		}

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

	public void setModel(Integer index) {

		PieChartEntityModel curr = pieChartEntityModels.get(index);
		MeasurableQuantity mq = curr.getPieChart().getMeasurableQuantity();
		if (!curr.getMinDate().before(curr.getMaxDate())) {
			curr.setMaxDate(curr.getMinDate());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(curr.getMaxDate());
		cal.add(Calendar.DATE, 1);
		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
				curr.getMinDate(), cal.getTime(), null, mq);

		PieChartModel chartModel = new PieChartModel();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(curr.getMinDate()));

		if (mvs.size() > 0) {
			for (MeasuredValue measuredValue : mvs) {
				chartModel.set(sdf.format(measuredValue.getDate()),
						measuredValue.getValue());
			}
		} else {
			log.info("No measured values found for : " + mq.getCode());
		}

		curr.setPieChart(curr.getPieChart());
		curr.setModel(chartModel);

	}

	public List<PieChartEntityModel> getPieChartEntityModels() {
		if (pieChartEntityModels.size() <= 0) {
			initChartModelList();
		}
		return pieChartEntityModels;
	}

	public void setPieChartEntityModels(
			List<PieChartEntityModel> pieChartEntityModels) {
		this.pieChartEntityModels = pieChartEntityModels;
	}

}
