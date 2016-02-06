
package org.meveocrm.admin.action.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.model.dwh.PieChart;
import org.meveocrm.services.dwh.ChartService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;


public  class ChartEntityBean<T extends Chart,CM extends ChartModel,EM extends ChartEntityModel<T,CM>> extends BaseBean<T> {

	@Inject
	protected ChartService<T> chartService;

	@Inject
	protected MeasuredValueService mvService;

	protected EM chartEntityModel;

	protected List<EM> chartEntityModels = new ArrayList<EM>();

	
	private static final long serialVersionUID = 5241132812597358412L;

	public ChartEntityBean() {
		super();
	}

	public ChartEntityBean(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected IPersistenceService<T> getPersistenceService() {
		return chartService;
	}

	@Override
	protected String getListViewName() {
		return "charts";
	}
	
	@SuppressWarnings("unchecked")
	public List<EM> initChartModelList() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

		chartEntityModels = new ArrayList<EM>();
		List<T> chartList = chartService.list();

		for (T chart : chartList) {
			MeasurableQuantity mq = chart.getMeasurableQuantity();
			List<MeasuredValue> mvs = mvService
					.getByDateAndPeriod(null, fromDate.getTime(), toDate.getTime(), null, mq);


			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
			ChartSeries mvSeries = new ChartSeries();

			mvSeries.setLabel(sdf.format(fromDate.getTime()));

			if (mvs.size() > 0) {
				for (MeasuredValue measuredValue : mvs) {
					mvSeries.set(sdf.format(measuredValue.getDate()), measuredValue.getValue());
				}
			} else {
				mvSeries.set("NO RECORDS", 0);
				log.info("No measured values found for : " + mq.getCode());
			}
			boolean isAdmin = chart.getAuditable().getCreator().hasRole("administrateur");
			boolean equalUser = chart.getAuditable().getCreator().getId() == getCurrentUser().getId();
			boolean sameRoleWithChart = chart.getRole() != null ? getCurrentUser().hasRole(
					chart.getRole().getDescription()) : false;
			chart.setVisible(isAdmin || equalUser || sameRoleWithChart);
			if(chart instanceof BarChart){
				BarChartModel chartModel = new BarChartModel();
				chartModel.addSeries(mvSeries);
				chartModel.setTitle(mq.getDescription());
				configureBarChartModel(chartModel, (BarChart)chart);
				BarChartEntityModel chartEntityModel = new BarChartEntityModel();
				chartEntityModel.setModel(chartModel);
				chartEntityModel.setChart((BarChart) chart);
				chartEntityModels.add((EM) chartEntityModel);
				log.debug("add barChart model {}",mq.getCode());
			} else if (chart instanceof LineChart){
				LineChartModel chartModel = new LineChartModel();
				chartModel.addSeries(mvSeries);
				chartModel.setTitle(mq.getDescription());
				configureLineChartModel(chartModel, (LineChart)chart);
				LineChartEntityModel chartEntityModel = new LineChartEntityModel();
				chartEntityModel.setModel(chartModel);
				chartEntityModel.setChart((LineChart) chart);
				chartEntityModels.add((EM) chartEntityModel);
				log.debug("add lineChart model {}",mq.getCode());
			} else if (chart instanceof PieChart){
				PieChartModel chartModel = new PieChartModel();
				chartModel.setTitle(mq.getDescription());
				configurePieChartModel(chartModel, (PieChart)chart);
				PieChartEntityModel chartEntityModel = new PieChartEntityModel();
				chartEntityModel.setModel(chartModel);
				chartEntityModel.setChart((PieChart) chart);
				chartEntityModels.add((EM) chartEntityModel);
				log.debug("add pieChart model {}",mq.getCode());
			}
		}
		return chartEntityModels;
	}




	@SuppressWarnings("unchecked")
	public EM getChartEntityModel() {
		if (chartEntityModel == null) {
			if(entity instanceof BarChart){
				chartEntityModel = (EM) new BarChartEntityModel();
			}
		}
		if (entity != null) {
			if (entity.getMeasurableQuantity() != null) {
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(Calendar.DAY_OF_MONTH, 1);
				Calendar toDate = Calendar.getInstance();
				toDate.setTime(fromDate.getTime());
				toDate.add(Calendar.MONTH, 1);

				MeasurableQuantity mq = getEntity().getMeasurableQuantity();
				List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null, fromDate.getTime(), toDate.getTime(),
						null, mq);


				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
				ChartSeries mvSeries = new ChartSeries();

				mvSeries.setLabel(sdf.format(fromDate.getTime()));
				if (mvs.size() > 0) {
					for (MeasuredValue measuredValue : mvs) {
						mvSeries.set(sdf.format(measuredValue.getDate()), measuredValue.getValue());
					}

				} else {
					mvSeries.set("NO RECORDS", 0);
					mvSeries.set("SAMPLE RECORD", 10);
					mvSeries.set("SAMPLE RECORD 1", 20);

					log.info("No measured values found for : " + mq.getCode());
				}
				if(entity instanceof BarChart){
					BarChartModel chartModel = new BarChartModel();
					chartModel.addSeries(mvSeries);
					chartModel.setTitle(mq.getDescription());
					configureBarChartModel(chartModel, (BarChart) entity);
					chartEntityModel.setModel((CM) chartModel);
				} else if(entity instanceof LineChart){
					LineChartModel chartModel = new LineChartModel();
					chartModel.addSeries(mvSeries);
					chartModel.setTitle(mq.getDescription());
					configureLineChartModel(chartModel, (LineChart) entity);
					chartEntityModel.setModel((CM) chartModel);
				} else if(entity instanceof PieChart){
					PieChartModel chartModel = new PieChartModel();
					chartModel.setTitle(mq.getDescription());
					configurePieChartModel(chartModel, (PieChart) entity);
					chartEntityModel.setModel((CM) chartModel);
				}
				chartEntityModel.setChart(getEntity());
			}
		}
		return chartEntityModel;
	}

	public void setChartEntityModel(EM chartEntityModel) {
		this.chartEntityModel = chartEntityModel;
	}

	@SuppressWarnings("unchecked")
	public void setModel(Integer modelIndex) {
		EM curr = chartEntityModels.get(modelIndex);
		MeasurableQuantity mq = curr.getChart().getMeasurableQuantity();
		if (!curr.getMinDate().before(curr.getMaxDate())) {
			curr.setMaxDate(curr.getMinDate());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(curr.getMaxDate());
		cal.add(Calendar.DATE, 1);

		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(null, curr.getMinDate(), cal.getTime(), null, mq);


		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(curr.getMinDate()));

		if (mvs.size() > 0) {
			for (MeasuredValue measuredValue : mvs) {
				mvSeries.set(sdf.format(measuredValue.getDate()), measuredValue.getValue());
			}

		} else {
			mvSeries.set("NO RECORDS", 0);
			log.info("No measured values found for : " + mq.getCode());
		}
		if(curr.getChart() instanceof BarChart){
			BarChartModel chartModel = new BarChartModel();
			chartModel.addSeries(mvSeries);
			chartModel.setTitle(mq.getDescription());
			configureBarChartModel(chartModel, (BarChart) curr.getChart());
			curr.setModel((CM) chartModel);
		} else if(curr.getChart() instanceof LineChart){
			LineChartModel chartModel = new LineChartModel();
			chartModel.addSeries(mvSeries);
			chartModel.setTitle(mq.getDescription());
			configureLineChartModel(chartModel, (LineChart) curr.getChart());
			curr.setModel((CM) chartModel);
		}
		curr.setChart(curr.getChart());
	}

	public List<EM> getChartEntityModels() {
		if (chartEntityModels.size() <= 0) {
			initChartModelList();
		}
		return chartEntityModels;
	}

	public void setChartEntityModels(List<EM> chartEntityModels) {
		this.chartEntityModels = chartEntityModels;
	}

	private void configureBarChartModel(BarChartModel chartModel, BarChart barChart) {
		if (barChart.getExtender() != null) {
			chartModel.setExtender(entity.getExtender());
		}

		chartModel.setStacked(barChart.isStacked());

		Axis xAxis = chartModel.getAxis(AxisType.X);

		if (!StringUtils.isBlank(barChart.getXaxisLabel())) {
			xAxis.setLabel(barChart.getXaxisLabel());
		}

		if (barChart.getXaxisAngle() != null) {
			xAxis.setTickAngle(barChart.getXaxisAngle());
		}
		Axis yAxis = chartModel.getAxis(AxisType.Y);
		if (!StringUtils.isBlank(barChart.getYaxisLabel())) {
			yAxis.setLabel(barChart.getYaxisLabel());
		}
		yAxis.setMin(barChart.getMin());
		yAxis.setMax(barChart.getMax() != null && barChart.getMax() != 0 ? barChart.getMax() : null);
		if (barChart.getYaxisAngle() != null) {
			yAxis.setTickAngle(barChart.getYaxisAngle());
		}

		chartModel.setLegendCols(barChart.getLegendCols());
		chartModel.setLegendRows(barChart.getLegendRows());
		chartModel.setZoom(barChart.isZoom());
		chartModel.setAnimate(barChart.isAnimate());
		chartModel.setShowDatatip(barChart.isShowDataTip());
		if (barChart.getDatatipFormat() != null) {
			chartModel.setDatatipFormat(barChart.getDatatipFormat());
		}
	}

	private void configureLineChartModel(LineChartModel chartModel, LineChart lineChart) {
		if (lineChart.getExtender() != null) {
			chartModel.setExtender(lineChart.getExtender());
		}

		if (lineChart.getLegendPosition() != null) {
			chartModel.setLegendPosition(lineChart.getLegendPosition().name());
		}
		chartModel.setSeriesColors(lineChart.getSeriesColors());
		chartModel.setShadow(lineChart.isShadow());

		Axis xAxis = chartModel.getAxis(AxisType.X);
		if (!StringUtils.isBlank(lineChart.getXaxisLabel())) {
			xAxis.setLabel(lineChart.getXaxisLabel());
		}

		xAxis.setMin(lineChart.getMinX());
		xAxis.setMax(lineChart.getMaxX());
		xAxis.setMax(lineChart.getMaxX() != 0 ? lineChart.getMaxX() : null);
		if (lineChart.getXaxisAngle() != null) {
			xAxis.setTickAngle(lineChart.getXaxisAngle());
		}

		Axis yAxis = chartModel.getAxis(AxisType.Y);
		if (!StringUtils.isBlank(lineChart.getYaxisLabel())) {
			xAxis.setLabel(lineChart.getYaxisLabel());
		}
		yAxis.setMin(lineChart.getMinY());
		yAxis.setMax(lineChart.getMaxY() != 0 ? lineChart.getMaxY() : null);
		if (lineChart.getYaxisAngle() != null) {
			yAxis.setTickAngle(lineChart.getYaxisAngle());
		}

		chartModel.setBreakOnNull(lineChart.isBreakOnNull());

		chartModel.setLegendCols(lineChart.getLegendCols());
		chartModel.setLegendRows(lineChart.getLegendRows());
		chartModel.setStacked(lineChart.isStacked());
		chartModel.setZoom(lineChart.isZoom());
		chartModel.setAnimate(lineChart.isAnimate());
		chartModel.setShowDatatip(lineChart.isShowDataTip());

		if (lineChart.getDatatipFormat() != null) {
			chartModel.setDatatipFormat(lineChart.getDatatipFormat());
		}
	}
	
	private void configurePieChartModel(PieChartModel chartModel, PieChart pieChart) {
		if (pieChart.getExtender() != null) {
			chartModel.setExtender(pieChart.getExtender());
		}

		chartModel.setFill(pieChart.isFilled());
		if (pieChart.getLegendPosition() != null) {
			chartModel.setLegendPosition(pieChart.getLegendPosition().name());
		}
		chartModel.setSeriesColors(pieChart.getSeriesColors());
		if (pieChart.getDiameter() != null) {
			chartModel.setDiameter(pieChart.getDiameter());
		}

		chartModel.setSliceMargin(pieChart.getSliceMargin());
		chartModel.setShadow(pieChart.isShadow());
		chartModel.setShowDataLabels(pieChart.isShowDataLabels());
		chartModel.setLegendCols(pieChart.getLegendCols());
		chartModel.setLegendRows(pieChart.getLegendRows());
	}
}
