package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.ChartService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

@Named
@ConversationScoped
public class ChartBean extends BaseBean<Chart> {

	private static final long serialVersionUID = 2585685452044860823L;

	@Inject
	ChartService chartService;

	@Inject
	MeasuredValueService mvService;

	private CartesianChartModel chartModel;

	private Date selectedDate;

	public ChartBean() {
		super(Chart.class);

	}

	protected IPersistenceService<Chart> getPersistenceService() {
		return chartService;
	}

	protected String getDefaultViewName() {
		return "charts";
	}

	@Override
	protected String getListViewName() {
		return "charts";
	}

	public CartesianChartModel getChartModel() {
		if (getEntity() != null && selectedDate != null) {
			chartModel = new CartesianChartModel();
			Calendar fromDate = Calendar.getInstance();
			fromDate.setTime(selectedDate);
			fromDate.set(Calendar.DAY_OF_MONTH, 1);
			Calendar toDate = Calendar.getInstance();
			toDate.setTime(fromDate.getTime());
			toDate.add(Calendar.MONTH, 1);

			List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null,
					fromDate.getTime(), toDate.getTime(), null, getEntity()
							.getMeasurableQuantity());

			ChartSeries mvSeries = new ChartSeries("Values");

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");

			if (mvs.size() > 0) {
				for (MeasuredValue measuredValue : mvs) {
					log.info(measuredValue.getDate().toString());
					mvSeries.set(sdf.format(measuredValue.getDate()),
							measuredValue.getValue());
				}
			} else {
				mvSeries.set("No Values retrieved", 0);
			}

			chartModel.addSeries(mvSeries);
		}
		return chartModel;
	}

	public void setChartModel(CartesianChartModel chartModel) {
		this.chartModel = chartModel;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}

}
