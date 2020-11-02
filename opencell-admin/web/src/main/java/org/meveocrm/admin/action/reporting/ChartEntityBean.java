/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveocrm.admin.action.reporting;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.dwh.BarChart;
import org.meveo.model.dwh.Chart;
import org.meveo.model.dwh.LineChart;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.model.dwh.PieChart;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.order.OrderService;
import org.meveocrm.services.dwh.ChartService;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

public class ChartEntityBean<T extends Chart, CM extends ChartModel, EM extends ChartEntityModel<T, CM>> extends BaseBean<T> {

	@Inject
	protected ChartService<T> chartService;

	@Inject
	protected MeasuredValueService mvService;

	@Inject
	protected MeasurableQuantityService mqService;

	@Inject
	private OrderService orderService;

	protected EM chartEntityModel;

	protected List<EM> chartEntityModels = null;

	private static final long serialVersionUID = 5241132812597358412L;

	public ChartEntityBean() {
		super();
		showDeprecatedWarning();
	}

	public ChartEntityBean(Class<T> clazz) {
		super(clazz);
		showDeprecatedWarning();
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
	public EM buildChargeEntityModel(Date fromDate, Date toDate, MeasurableQuantity mq, T chart, String dimension1, String dimension2, String dimension3, String dimension4) {
		EM result = null;
		// log.debug(
		// "buildChargeEntityModel {} from {} to {}, dimension1={}, dimension2={},"
		// + " dimension3={}, dimension4={}",
		// mq.getCode(), fromDate, toDate, dimension1, dimension2, dimension3,
		// dimension4);
		List<MeasuredValue> mvs = mvService.getByDateAndPeriod(mq.getCode(), fromDate, toDate, mq.getMeasurementPeriod(), mq);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ChartSeries mvSeries = new ChartSeries();

		mvSeries.setLabel(sdf.format(fromDate.getTime()));
		boolean empty = mvs.size() == 0;
		if (!empty) {
			empty = true;
			Map<String, BigDecimal> aggregatedValues = new HashMap<String, BigDecimal>();
			Map<String, List<String>> aggregatedCount = new HashMap<String, List<String>>();
			boolean additive = mq.isAdditive();
			for (MeasuredValue measuredValue : mvs) {
				String key = sdf.format(measuredValue.getDate());
				// log.debug("md key={}, aggVal={}, dim1={},dim2={},dim3={},dim4={},",
				// key, aggregatedValues.get(key),
				// measuredValue.getDimension1(), measuredValue.getDimension2(),
				// measuredValue.getDimension3(),
				// measuredValue.getDimension4());
				if (measuredValue.getValue() != null && (StringUtils.isBlank(dimension1) || dimension1.equals(measuredValue.getDimension1()))
						&& (StringUtils.isBlank(dimension2) || dimension2.equals(measuredValue.getDimension2()))
						&& (StringUtils.isBlank(dimension3) || dimension3.equals(measuredValue.getDimension3()))
						&& (StringUtils.isBlank(dimension4) || dimension4.equals(measuredValue.getDimension4()))) {
					if (aggregatedValues.containsKey(key) && aggregatedValues.get(key) != null) {
						aggregatedValues.put(key, aggregatedValues.get(key).add(measuredValue.getValue()));
						if (!additive) {
							String aggregationkey = StringUtils.isBlank(dimension1) ? "" : dimension1;
							if (!aggregatedCount.get(key).contains(aggregationkey)) {
								aggregatedCount.get(key).add(aggregationkey);
							}
						}
						// log.debug("md key={}, aggVal>{}", key,
						// aggregatedValues.get(key));
					} else {
						aggregatedValues.put(key, measuredValue.getValue());
						if (!additive) {
							String aggregationkey = StringUtils.isBlank(dimension1) ? "" : dimension1;
							List<String> val = new ArrayList<>();
							val.add(aggregationkey);
							aggregatedCount.put(key, val);
						}
						// log.debug("md key={}, aggVal={}", key,
						// aggregatedValues.get(key));
					}
					if (empty) {
						empty = false;
					}
				}
			}
			List<String> keyList = new ArrayList<String>(aggregatedValues.keySet());
			Collections.sort(keyList);
			Collections.reverseOrder();
			for (String key : keyList) {
				mvSeries.set(key, additive ? aggregatedValues.get(key) : (aggregatedValues.get(key).divide(new BigDecimal(aggregatedCount.get(key).size()))));
			}
		}
		if (empty) {
			mvSeries.set("NO RECORDS", 0);
			log.info("No measured values found for : " + mq.getCode());
		}
		boolean isAdmin = currentUser.hasRole("administrateur");
		boolean equalUser = chart.getAuditable().isCreator(currentUser) ;
		boolean sameRoleWithChart = chart.getRole() != null ? currentUser.hasRole(chart.getRole().getDescription()) : false;
		chart.setVisible(isAdmin || equalUser || sameRoleWithChart);
		if (chart instanceof BarChart) {
			BarChartModel chartModel = new BarChartModel();
			chartModel.addSeries(mvSeries);
			chartModel.setTitle(mq.getDescription());
			configureBarChartModel(chartModel, (BarChart) chart);
			BarChartEntityModel chartEntityModel = new BarChartEntityModel();
			chartEntityModel.setModel(chartModel);
			chartEntityModel.setChart((BarChart) chart);
			result = (EM) chartEntityModel;
		} else if (chart instanceof LineChart) {
			LineChartModel chartModel = new LineChartModel();
			chartModel.addSeries(mvSeries);
			chartModel.setTitle(mq.getDescription());
			configureLineChartModel(chartModel, (LineChart) chart, mvSeries);
			LineChartEntityModel chartEntityModel = new LineChartEntityModel();
			chartEntityModel.setModel(chartModel);
			chartEntityModel.setChart((LineChart) chart);
			result = (EM) chartEntityModel;
		} else if (chart instanceof PieChart) {
			PieChartModel chartModel = new PieChartModel();
			chartModel.setTitle(mq.getDescription());
			configurePieChartModel(chartModel, (PieChart) chart, mvSeries);
			PieChartEntityModel chartEntityModel = new PieChartEntityModel();
			chartEntityModel.setModel(chartModel);
			chartEntityModel.setChart((PieChart) chart);
			result = (EM) chartEntityModel;
		}
		if (!empty && result != null) {
			result.setDimension1List(mvService.getDimensionList(1, fromDate, toDate, mq));
			result.setDimension2List(mvService.getDimensionList(2, fromDate, toDate, mq));
			result.setDimension3List(mvService.getDimensionList(3, fromDate, toDate, mq));
			result.setDimension4List(mvService.getDimensionList(4, fromDate, toDate, mq));
		}
		return result;
	}

	public List<EM> initChartModelList() {
		log.debug("initChartModelList");
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.setTime(fromDate.getTime());
		toDate.add(Calendar.MONTH, 1);

		chartEntityModels = new ArrayList<EM>();
		List<T> chartList = chartService.list();

		for (T chart : chartList) {
			MeasurableQuantity mq = chart.getMeasurableQuantity();
			EM chartEntityModel = buildChargeEntityModel(fromDate.getTime(), toDate.getTime(), mq, chart, null, null, null, null);
			chartEntityModels.add(chartEntityModel);
			log.debug("add model {}", mq.getCode());
		}
		return chartEntityModels;
	}

	public EM getChartEntityModel() {
		log.debug("getChartEntityModel");
		if (chartEntityModel == null) {
			if (entity != null && entity.getMeasurableQuantity() != null) {
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(Calendar.DAY_OF_MONTH, 1);
				Calendar toDate = Calendar.getInstance();
				toDate.setTime(fromDate.getTime());
				toDate.add(Calendar.MONTH, 1);
				MeasurableQuantity mq = getEntity().getMeasurableQuantity();
				chartEntityModel = buildChargeEntityModel(fromDate.getTime(), toDate.getTime(), mq, entity, null, null, null, null);
			}
		}
		return chartEntityModel;
	}

	public void setChartEntityModel(EM chartEntityModel) {
		this.chartEntityModel = chartEntityModel;
	}

	public void setModel(Integer modelIndex) {
		log.debug("setModel index={}", modelIndex);
		EM curr = chartEntityModels.get(modelIndex);
		MeasurableQuantity mq = curr.getChart().getMeasurableQuantity();
		curr.setModel(buildChargeEntityModel(curr.getMinDate(), curr.getMaxDate(), mq, curr.getChart(), curr.getDimension1(), curr.getDimension2(), curr.getDimension3(),
				curr.getDimension4()).getModel());
	}

	public List<EM> getChartEntityModels() {
        if (chartEntityModels == null) {
            initChartModelList();
        }
		return chartEntityModels;
	}

	public void setChartEntityModels(List<EM> chartEntityModels) {
		this.chartEntityModels = chartEntityModels;
	}

	private void configureBarChartModel(BarChartModel chartModel, BarChart barChart) {
		if (barChart.getExtender() != null) {
			chartModel.setExtender(barChart.getExtender());
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

	private void configureLineChartModel(LineChartModel chartModel, LineChart lineChart, ChartSeries chartSeries) {

		LineChartSeries lineChartSeries = new LineChartSeries();
		for (Object o : chartSeries.getData().keySet()) {
			lineChartSeries.set(o.toString(), chartSeries.getData().get(o));
		}
		chartModel.addSeries(lineChartSeries);

		if (lineChart.getExtender() != null) {
			chartModel.setExtender(lineChart.getExtender());
		}

		if (lineChart.getLegendPosition() != null) {
			chartModel.setLegendPosition(lineChart.getLegendPosition().name());
		}
		chartModel.setSeriesColors(lineChart.getSeriesColors());
		chartModel.setShadow(lineChart.isShadow());

		Axis xAxis = chartModel.getAxes().put(AxisType.X, new CategoryAxis("Dates"));
		if (!StringUtils.isBlank(lineChart.getXaxisLabel())) {
			xAxis = chartModel.getAxes().put(AxisType.X, new CategoryAxis(lineChart.getXaxisLabel()));
		}

		/*
		 * if (lineChart.getMinX() < lineChart.getMaxX()) {
		 * xAxis.setMin(lineChart.getMinX()); xAxis.setMax(lineChart.getMaxX());
		 * }
		 */
		if (lineChart.getXaxisAngle() != null) {
			xAxis.setTickAngle(lineChart.getXaxisAngle());
		}

		Axis yAxis = chartModel.getAxis(AxisType.Y);
		if (!StringUtils.isBlank(lineChart.getYaxisLabel())) {
			yAxis.setLabel(lineChart.getYaxisLabel());
		}
		if (lineChart.getMinY() < lineChart.getMaxY()) {
			yAxis.setMin(lineChart.getMinY());
			yAxis.setMax(lineChart.getMaxY());
		}
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

	private void configurePieChartModel(PieChartModel chartModel, PieChart pieChart, ChartSeries chartSeries) {

		for (Object o : chartSeries.getData().keySet()) {
			chartModel.set(o.toString(), chartSeries.getData().get(o));
		}
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

	public List<StatModel> getStats() {
		List<StatModel> result = new ArrayList<StatModel>();
		List<MeasurableQuantity> mqList = mqService.list();

		if (mqList.size() > 0) {
			for (MeasurableQuantity mq : mqList) {
				StatModel sm = new StatModel();
				List<MeasuredValue> mvCurrentPeriod = mvService.getByDateAndPeriod(null, null, null, mq.getMeasurementPeriod(), mq, true);

				if (mvCurrentPeriod.size() > 0) {
					MeasuredValue mv1 = mvCurrentPeriod.get(mvCurrentPeriod.size() - 1);
					sm.setValue(mv1.getValue().doubleValue());
					MeasuredValue mv2 = null;

					if (mvCurrentPeriod.size() > 1) {
						mv2 = mvCurrentPeriod.get(mvCurrentPeriod.size() - 2);
					} else {
						mv2 = new MeasuredValue();
						mv2.setValue(new BigDecimal("0.00"));
					}

					sm.setDescription(mq.getDescription() != null && mq.getDescription().length() > 0 ? mq.getDescription() : mq.getCode());
					sm.computeDifference(mv2.getValue().doubleValue());

					sm.setLastUpdated(mv1.getDate());
					sm.setValue(mv1.getValue().doubleValue());
					result.add(sm);
				}
			}
		}
		return result;
	}

	public StatModel getNewOrderStats() {

		Calendar currentDate = Calendar.getInstance();
		Calendar beforeDate = Calendar.getInstance();

		beforeDate.add(Calendar.DATE, -1);
		beforeDate.set(Calendar.HOUR_OF_DAY, 23);
		beforeDate.set(Calendar.MINUTE, 59);
		beforeDate.set(Calendar.SECOND, 59);
		StatModel result = new StatModel();

		result.setDescription("New Orders");
		result.setLastUpdated(currentDate.getTime());
		result.setValue(orderService.countNewOrders(currentDate).doubleValue());
		result.computeDifference(orderService.countNewOrders(beforeDate).doubleValue());

		return result;
	}

	public StatModel getPendingOrderStats() {
		Calendar currentDate = Calendar.getInstance();
		Calendar beforeDate = Calendar.getInstance();
		beforeDate.add(Calendar.DATE, -1);
		StatModel result = new StatModel();

		result.setDescription("Pending Orders");
		result.setLastUpdated(Calendar.getInstance().getTime());
		result.setValue(orderService.countPendingOrders(currentDate).doubleValue());
		result.computeDifference(orderService.countPendingOrders(beforeDate).doubleValue());

		return result;
	}

	public StatModel getChurn() throws BusinessException {
		Calendar currentDate = Calendar.getInstance();
		Calendar beforeDate = Calendar.getInstance();

		currentDate.set(Calendar.DATE, 1);
		beforeDate.set(Calendar.DATE, 1);
		beforeDate.add(Calendar.MONTH, -1);
		StatModel result = new StatModel();

		MeasurableQuantity mq = mqService.findByCode("CHURN");

		if (mq == null) {
			mq = new MeasurableQuantity();
			mq.setCode("CHURN");
			mq.setActive(true);
			mq.setDescription("Churn");
			mq.setSqlQuery("WITH acc_new AS (SELECT count(ua.id) as nb FROM billing_user_account ua JOIN account_entity ae ON ae.id=ua.id"
					+ " WHERE ua.subscription_date >= '#{date}' AND ua.subscription_date < '#{nextDate}'"
					+ " AND (ua.termination_date IS NULL OR ua.termination_date >= '#{nextDate}') ), acc_end AS ("
					+ " SELECT count(ua.id) as nb FROM billing_user_account ua JOIN account_entity ae ON ae.id=ua.id WHERE ua.termination_date >= ' #{date}'"
					+ " AND ua.termination_date < '#{nextDate}' ), acc_period AS ("
					+ " SELECT count(ua.id) as nb FROM billing_user_account ua JOIN account_entity ae ON ae.id=ua.id WHERE ua.subscription_date < '#{date}'"
					+ " AND (ua.termination_date IS NULL  OR ua.termination_date>='#{nextDate}') " + ") SELECT"
					+ " CASE WHEN (acc_period.nb+(acc_new.nb+acc_end.nb)/2)<>0 THEN acc_end.nb/(acc_period.nb +(acc_new.nb+acc_end.nb)/2) ELSE 0"
					+ " END as churn FROM acc_new ,acc_end,acc_period");
			mq.setEditable(true);
			mq.setMeasurementPeriod(MeasurementPeriodEnum.MONTHLY);
			mqService.create(mq);
		}

		List<MeasuredValue> currentMVs = mvService.getByDateAndPeriod("CHURN", currentDate.getTime(), null, MeasurementPeriodEnum.MONTHLY, mq);
		MeasuredValue currentMV = (currentMVs != null && currentMVs.size() > 0) ? currentMVs.get(0) : new MeasuredValue();
		Double currentValue = currentMV != null && currentMV.getValue() != null ? currentMV.getValue().doubleValue() : new Double("0.00");

		List<MeasuredValue> beforeMVs = mvService.getByDateAndPeriod("CHURN", beforeDate.getTime(), null, MeasurementPeriodEnum.MONTHLY, mq);
		MeasuredValue beforeMV = (beforeMVs != null && beforeMVs.size() > 0) ? beforeMVs.get(0) : new MeasuredValue();
		Double beforeValue = beforeMV != null && beforeMV.getValue() != null ? beforeMV.getValue().doubleValue() : new Double("0.00");

		log.info("Current MV : " + currentValue + ", Before MV : " + beforeValue);
		result.setValue(currentValue);
		result.setDescription("Churn");
		result.computeDifference(beforeValue);
		result.setLastUpdated(currentDate.getTime());
		return result;
	}

	public StatModel getMRR() throws BusinessException {
		Calendar currentDate = Calendar.getInstance();
		Calendar beforeDate = Calendar.getInstance();

		currentDate.set(Calendar.DATE, 1);
		beforeDate.set(Calendar.DATE, 1);
		beforeDate.add(Calendar.MONTH, -1);
		StatModel result = new StatModel();

		MeasurableQuantity mq = mqService.findByCode("MRR");

		if (mq == null) {
			mq = new MeasurableQuantity();
			mq.setCode("MRR");
			mq.setActive(true);
			mq.setDescription("Churn");
			mq.setDimension1("Offer");
			mq.setDimension2("BA");
			mq.setDimension3("CA");
			mq.setDimension4("Cust");
			mq.setEditable(true);
			mq.setMeasurementPeriod(MeasurementPeriodEnum.MONTHLY);
			mq.setSqlQuery("SELECT  wo.amount_without_tax/((EXTRACT(epoch FROM (wo.end_date - wo.start_date))/86400)) as mrr_value,"
					+ " wo.offer_code as offer_code,ba.code as ba_code,ca.code as ca_code,cust.code as cust_code FROM billing_wallet_operation wo"
					+ "	LEFT JOIN billing_charge_instance ci ON ci.id=wo.charge_instance_id"
					+ "	LEFT JOIN billing_subscription sub ON sub.id = ci.subscription_id LEFT JOIN billing_wallet w ON w.id=wo.wallet_id"
					+ "	LEFT JOIN billing_user_account uaa ON uaa.id= w.user_account_id	LEFT JOIN account_entity ua ON ua.id= uaa.id"
					+ "	LEFT JOIN billing_billing_account baa ON baa.id= uaa.billing_account_id	LEFT JOIN account_entity ba ON ba.id= baa.id"
					+ "	LEFT JOIN ar_customer_account caa ON caa.id= baa.customer_account_id LEFT JOIN account_entity ca ON ca.id= caa.id"
					+ "	LEFT JOIN crm_customer custa ON custa.id= caa.customer_id LEFT JOIN account_entity cust ON cust.id= custa.id"
					+ "	WHERE NOT(wo.end_date IS NULL)"
					+ "	AND ((EXTRACT(epoch FROM (wo.end_date - wo.start_date))/86400))>0 AND (wo.start_date <= '#{date}') AND (wo.end_date > '#{date}')");
			mqService.create(mq);
		}

		List<MeasuredValue> currentMVs = mvService.getByDateAndPeriod("MRR", currentDate.getTime(), null, MeasurementPeriodEnum.MONTHLY, mq);
		MeasuredValue currentMV = (currentMVs != null && currentMVs.size() > 0) ? currentMVs.get(0) : new MeasuredValue();
		Double currentValue = currentMV != null && currentMV.getValue() != null ? currentMV.getValue().doubleValue() : new Double("0.00");

		List<MeasuredValue> beforeMVs = mvService.getByDateAndPeriod("MRR", beforeDate.getTime(), null, MeasurementPeriodEnum.MONTHLY, mq);
		MeasuredValue beforeMV = (beforeMVs != null && beforeMVs.size() > 0) ? beforeMVs.get(0) : new MeasuredValue();
		Double beforeValue = beforeMV != null && beforeMV.getValue() != null ? beforeMV.getValue().doubleValue() : new Double("0.00");
		result.setValue(currentValue);
		result.setDescription("MRR");
		result.computeDifference(beforeValue);
		result.setLastUpdated(currentDate.getTime());
		return result;
	}

	public List<MeasuredValue> getMeasuredValuesPerYear(String code, String mqCode) throws BusinessException {
		return getMeasuredValues(code, mqCode, -13, Calendar.MONTH);
	}

	public List<MeasuredValue> getMeasuredValues(String code, String mqCode, int countFromNow, int period) throws BusinessException {
		Calendar currentDate = Calendar.getInstance();
		Calendar beforeDate = Calendar.getInstance();

		currentDate.set(Calendar.DATE, 1);
		beforeDate.set(Calendar.DATE, 1);
		beforeDate.add(period, countFromNow);

		List<MeasuredValue> result = new ArrayList<>();

		MeasurableQuantity mq = mqService.findByCode(mqCode);
		List<MeasuredValue> measuredValues = mvService.getByDateAndPeriod(code, beforeDate.getTime(), currentDate.getTime(),mq == null ? null: mq.getMeasurementPeriod(), mq);
		if(measuredValues != null){
			for(MeasuredValue value : measuredValues){
				result.add(value);
			}
		}

		return result;
	}
}
