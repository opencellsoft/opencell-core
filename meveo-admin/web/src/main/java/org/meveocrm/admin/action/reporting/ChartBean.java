/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveocrm.admin.action.reporting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.dwh.BarChart;
import org.meveo.model.dwh.Chart;
import org.meveo.model.dwh.LineChart;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.PieChart;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.ChartModel;

import com.google.gson.Gson;

@Named
@ViewScoped
public class ChartBean extends ChartEntityBean<Chart, ChartModel, ChartEntityModel<Chart, ChartModel>> {

    @Inject
    private CustomFieldInstanceService cfiService;

    private static final long serialVersionUID = 2585685452044860823L;

    public ChartBean() {
        super(Chart.class);
    }

    public ChartBean(Class<Chart> clazz) {
        super(clazz);
    }

    public String getEditView(Chart chart) {

        if (chart instanceof BarChart) {
            return "/pages/reporting/dwh/barChartDetail.xhtml";
        }
        if (chart instanceof PieChart) {
            return "/pages/reporting/dwh/pieChartDetail.xhtml";
        }
        if (chart instanceof LineChart) {
            return "/pages/reporting/dwh/lineChartDetail.xhtml";
        }
        return "/pages/reporting/dwh/barChartDetail.xhtml";
    }

    public String getMrrOnSubscriptionsValues() throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM");
        List<MeasuredValue> measuredValues = getMeasuredValuesPerYear(null, "MQ_MRR_REC_PER_MONTH_SUBS");
        ChartJsModel jsModel = new ChartJsModel();
        jsModel.getDatasets().put("regular", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("new", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("upsell", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("cancelled", new ArrayList<BigDecimal>());

        for (MeasuredValue value : measuredValues) {
            jsModel.getChartLabels().add(format.format(value.getDate()));
            jsModel.getDatasets().get("regular").add(new BigDecimal(value.getDimension1()));
            jsModel.getDatasets().get("new").add(new BigDecimal(value.getDimension2()));
            jsModel.getDatasets().get("upsell").add(new BigDecimal(value.getDimension3()));
            jsModel.getDatasets().get("cancelled").add(new BigDecimal(value.getDimension4()).multiply(BigDecimal.ONE.negate()));
        }
        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    public String getChurnValues() throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM");
        List<MeasuredValue> measuredValues = getMeasuredValuesPerYear(null, "MQ_CHURN_SUB_PER_MONTH");
        ChartJsModel jsModel = new ChartJsModel();
        jsModel.getDatasets().put("subscriptions", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("terminations", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("trend", new ArrayList<BigDecimal>());

        for (MeasuredValue value : measuredValues) {
            jsModel.getChartLabels().add(format.format(value.getDate()));
            jsModel.getDatasets().get("subscriptions").add(new BigDecimal(value.getDimension1()));
            jsModel.getDatasets().get("terminations").add(new BigDecimal(value.getDimension2()).multiply(BigDecimal.ONE.negate()));
            jsModel.getDatasets().get("trend").add(value.getValue());
        }

        jsModel.setTrendValue(computeAverageTrend(jsModel.getDatasets().get("trend")));

        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    public String getMrrOnOffers() throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM");
        List<MeasuredValue> measuredValues = getMeasuredValuesPerYear(null, "MQ_MRR_REC_PER_MONTH_PER_OFFER");
        ChartJsModel jsModel = new ChartJsModel();
        jsModel.getDatasets().put("offer1", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("offer2", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("offer3", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("offer4", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("others", new ArrayList<BigDecimal>());
        jsModel.getDatasets().put("total", new ArrayList<BigDecimal>());
        BigDecimal offer1;
        BigDecimal offer2;
        BigDecimal offer3;
        BigDecimal offer4;
        BigDecimal others;
        BigDecimal total;

        for (MeasuredValue value : measuredValues) {
            jsModel.getChartLabels().add(format.format(value.getDate()));
            offer1 = new BigDecimal(value.getDimension1());
            offer2 = new BigDecimal(value.getDimension2());
            offer3 = new BigDecimal(value.getDimension3());
            offer4 = new BigDecimal(value.getDimension4());
            others = value.getValue();
            total = offer1.add(offer2).add(offer3).add(offer4).add(others);
            jsModel.getDatasets().get("offer1").add(offer1);
            jsModel.getDatasets().get("offer2").add(offer2);
            jsModel.getDatasets().get("offer3").add(offer3);
            jsModel.getDatasets().get("offer4").add(offer4);
            jsModel.getDatasets().get("others").add(others);
            jsModel.getDatasets().get("total").add(total);
        }

        String labelsValue = (String)cfiService.getCFValue(appProvider, "CF_MQ_MRR_OFFER_LABELS");
        if (!StringUtils.isBlank(labelsValue)) {
            for (String label : labelsValue.split(",")) {
                jsModel.getLegendLabels().add(label);
            }
        }

        jsModel.setTrendValue(computeCompoundGrowthRate(jsModel.getDatasets().get("total")));

        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    public String getOrdersByStatus() throws BusinessException {       
        List<MeasuredValue> measuredValues = getMeasuredValues(null, "MQ_ORDERS_BY_STATUS", -31, Calendar.DAY_OF_MONTH);
        ChartJsModel jsModel = new ChartJsModel();

        jsModel.getLegendLabels().addAll(Arrays.asList("New", "Pending", "Validated", "Cancelled"));

        List<BigDecimal> data = new ArrayList<>();
        if(measuredValues != null && measuredValues.size() > 0){
	        MeasuredValue latestValue = measuredValues.get(measuredValues.size() - 1);
	        data.add(latestValue.getValue());
	        data.add(new BigDecimal(latestValue.getDimension1()));
	        data.add(new BigDecimal(latestValue.getDimension2()));
	        data.add(new BigDecimal(latestValue.getDimension3()));
        }
        jsModel.getDatasets().put("data", data);

        jsModel.setTrendValue(computeMeasuredValuesAverage(measuredValues));

        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    @Override
    public LazyDataModel<Chart> getLazyDataModel() {
        getFilters();
        if (filters.containsKey("user")) {
            filters.put("auditable.creator", filters.get("user"));
            filters.remove("user");
        }
        return super.getLazyDataModel();
    }

    private BigDecimal computeAverage(List<BigDecimal> values) {
        BigDecimal total = new BigDecimal(0);
        for (BigDecimal value : values) {
            total = total.add(value);
        }
        BigDecimal average = new BigDecimal(0);
        if (values.size() > 0) {
            average = total.divide(new BigDecimal(values.size()), 15, BigDecimal.ROUND_HALF_UP);
        }
        return average;
    }

    private BigDecimal computeAverageTrend(List<BigDecimal> trendList) {
    	if(trendList != null && trendList.size()>=10){    	
	        BigDecimal firstAverage = computeAverage(trendList.subList(0, 9));
	        BigDecimal lastAverage = computeAverage(trendList.subList(9, trendList.size()));
	        BigDecimal averageTrend = (firstAverage == null || firstAverage.compareTo(BigDecimal.ZERO) == 0) ?  BigDecimal.ZERO : lastAverage.divide(firstAverage, 15, RoundingMode.HALF_UP);
	        averageTrend = averageTrend.subtract(BigDecimal.ONE);
	        averageTrend = averageTrend.multiply(new BigDecimal(100));
	        return averageTrend.setScale(1, RoundingMode.HALF_UP);    	
    	}
    	return null;
    }

	private BigDecimal computeCompoundGrowthRate(List<BigDecimal> totals) {
		if (totals.size() > 0) {
			int count = totals.size();
			double first = totals.get(0).doubleValue();
			double last = totals.get(count - 1).doubleValue();
			double growthRate = Math.pow(last / first, 1.0d / count);
			if (Double.isNaN(growthRate) || Double.isInfinite(growthRate)) {
				return BigDecimal.ZERO;
			} else {

				growthRate -= 1;
				growthRate *= 100;
				return BigDecimal.valueOf(growthRate).setScale(1, RoundingMode.HALF_UP);
			}
		}

		return BigDecimal.ZERO;
	}

    private BigDecimal computeMeasuredValuesAverage(List<MeasuredValue> measuredValues) {
        BigDecimal average = BigDecimal.ZERO;
        boolean isEmpty = measuredValues == null || measuredValues.size() == 0;
        if (!isEmpty) {
            MeasuredValue last = measuredValues.get(measuredValues.size() - 1);
            double lastValue = last.getValue().doubleValue();
            boolean isLastValueZero = lastValue == 0;
            if (!isLastValueZero) {
                double total = 0.0d;
                for (MeasuredValue value : measuredValues) {
                    total += value.getValue().doubleValue();
                }
                double avg = total / lastValue;
                avg -= 1;
                avg *= 100;
                average = new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP);
            }
        }
        return average;
    }
}
