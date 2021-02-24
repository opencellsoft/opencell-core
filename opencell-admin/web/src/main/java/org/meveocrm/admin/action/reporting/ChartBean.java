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

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.PieChart;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.ChartModel;

import com.google.gson.Gson;

/**
 * @author Youssef IZEM
 * @lastModifiedVersion 5.2
 */
@Named
@ViewScoped
public class ChartBean extends ChartEntityBean<Chart, ChartModel, ChartEntityModel<Chart, ChartModel>> {

    @Inject
    private CustomFieldInstanceService cfiService;

    private static final long serialVersionUID = 2585685452044860823L;

    private static final String MQ_MRR_REC_PER_MONTH_PER_OFFER = "MQ_MRR_REC_PER_MONTH_PER_OFFER";
    private static final String MQ_MRR_REC_PER_MONTH_SUBS = "MQ_MRR_REC_PER_MONTH_SUBS";
    private static final String MQ_CHURN_SUB_PER_MONTH = "MQ_CHURN_SUB_PER_MONTH";
    private static final String MQ_ORDERS_BY_STATUS = "MQ_ORDERS_BY_STATUS";
    private static final String DIMENSION_1 = "dimension1";
    private static final String DIMENSION_2 = "dimension2";
    private static final String DIMENSION_3 = "dimension3";
    private static final String DIMENSION_4 = "dimension4";
    private static final String OTHERS = "others";
    private static final String TREND = "trend";
    private static final String TOTAL = "total";

    public ChartBean() {
        super(Chart.class);
    }
    
    public ChartBean(final Class<Chart> clazz) {
        super(clazz);
    }

    public final String getEditView(Chart chart) {

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

    public final String getMrrOnSubscriptionsValues() throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        List<MeasuredValue> measuredValues = getMeasuredValuesPerYear(null, MQ_MRR_REC_PER_MONTH_SUBS);
        ChartJsModel jsModel = initChartJsModel(MQ_MRR_REC_PER_MONTH_SUBS);

        for (MeasuredValue value : measuredValues) {
            jsModel.getChartLabels().add(format.format(value.getDate()));
            jsModel.getDatasets().get(DIMENSION_1).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension1(), "0")));
            jsModel.getDatasets().get(DIMENSION_2).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension2(), "0")));
            jsModel.getDatasets().get(DIMENSION_3).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension3(), "0")));
            jsModel.getDatasets().get(DIMENSION_4).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension4(), "0")).multiply(BigDecimal.ONE.negate()));
        }
        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    public final String getChurnValues() throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        List<MeasuredValue> measuredValues = getMeasuredValuesPerYear(null, MQ_CHURN_SUB_PER_MONTH);
        ChartJsModel jsModel = initChartJsModel(MQ_CHURN_SUB_PER_MONTH);

        for (MeasuredValue value : measuredValues) {
            jsModel.getChartLabels().add(format.format(value.getDate()));
            jsModel.getDatasets().get(DIMENSION_1).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension1(), "0")));
            jsModel.getDatasets().get(DIMENSION_2).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension2(), "0")).multiply(BigDecimal.ONE.negate()));
            jsModel.getDatasets().get(DIMENSION_3).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension2(), "0")));
            jsModel.getDatasets().get(DIMENSION_4).add(new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension2(), "0")));
            jsModel.getDatasets().get(TREND).add(value.getValue());
        }

        jsModel.setTrendValue(computeAverageTrend(jsModel.getDatasets().get(TREND)));

        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    public final String getMrrOnOffers() throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        List<MeasuredValue> measuredValues = getMeasuredValuesPerYear(null, MQ_MRR_REC_PER_MONTH_PER_OFFER);
        ChartJsModel jsModel = initChartJsModel(MQ_MRR_REC_PER_MONTH_PER_OFFER);

        for (MeasuredValue value : measuredValues) {
            jsModel.getChartLabels().add(format.format(value.getDate()));
            BigDecimal offer1 = new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension1(), "0"));
            BigDecimal offer2 = new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension2(), "0"));
            BigDecimal offer3 = new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension3(), "0"));
            BigDecimal offer4 = new BigDecimal(this.defaultIfEmptyOrNull(value.getDimension4(), "0"));
            BigDecimal others = value.getValue();
            BigDecimal total = offer1.add(offer2).add(offer3).add(offer4).add(others);
            jsModel.getDatasets().get(DIMENSION_1).add(offer1);
            jsModel.getDatasets().get(DIMENSION_2).add(offer2);
            jsModel.getDatasets().get(DIMENSION_3).add(offer3);
            jsModel.getDatasets().get(DIMENSION_4).add(offer4);
            jsModel.getDatasets().get(OTHERS).add(others);
            jsModel.getDatasets().get(TOTAL).add(total);
        }

        String labelsValue = (String) cfiService.getCFValue(appProvider, "CF_MQ_MRR_OFFER_LABELS");
        if (!StringUtils.isBlank(labelsValue)) {
            for (String label : labelsValue.split(",")) {
                jsModel.getLegendLabels().add(label);
            }
        }

        jsModel.setTrendValue(computeCompoundGrowthRate(jsModel.getDatasets().get(TOTAL)));

        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    public final String getOrdersByStatus() throws BusinessException {
        List<MeasuredValue> measuredValues = getMeasuredValues(null, MQ_ORDERS_BY_STATUS, -31, Calendar.DAY_OF_MONTH);
        ChartJsModel jsModel = new ChartJsModel();

        MeasurableQuantity mq = mqService.findByCode(MQ_ORDERS_BY_STATUS);
        boolean hasDimension1 = false;
        boolean hasDimension2 = false;
        boolean hasDimension3 = false;
        boolean hasDimension4 = false;
        String dimension1 = null;
        String dimension2 = null;
        String dimension3 = null;
        String dimension4 = null;

        if (mq != null) {
            dimension1 = mq.getDimension1();
            dimension2 = mq.getDimension2();
            dimension3 = mq.getDimension3();
            dimension4 = mq.getDimension4();
            if (dimension1 != null && !"null".equals(dimension1)) {
                hasDimension1 = true; 
            }
            if (dimension2 != null && !"null".equals(dimension2)) {
                hasDimension2 = true;
            }
            if (dimension3 != null && !"null".equals(dimension3)) {
                hasDimension3 = true;
            }
            if (dimension4 != null && !"null".equals(dimension4)) {
                hasDimension4 = true;
            }
            jsModel.setTitle(mq.getDescription());
        }

        List<BigDecimal> data = new ArrayList<>();
        List<String> legendLabels = jsModel.getLegendLabels();
        if (measuredValues != null && measuredValues.size() > 0) {
            MeasuredValue latestValue = measuredValues.get(measuredValues.size() - 1);
            data.add(latestValue.getValue());
            if (hasDimension1) {
                legendLabels.add(dimension1);
            }
            if (hasDimension2) {
                legendLabels.add(dimension2);
                data.add(new BigDecimal(this.defaultIfEmptyOrNull(latestValue.getDimension1(), "0")));
            }
            if (hasDimension3) {
                legendLabels.add(dimension3);
                data.add(new BigDecimal(this.defaultIfEmptyOrNull(latestValue.getDimension2(), "0")));
            }
            if (hasDimension4) {
                legendLabels.add(dimension4);
                data.add(new BigDecimal(this.defaultIfEmptyOrNull(latestValue.getDimension3(), "0")));
            }
        }
        jsModel.getDatasets().put("data", data);
        jsModel.setTrendValue(computeMeasuredValuesAverage(measuredValues));

        Gson gson = new Gson();
        return gson.toJson(jsModel);
    }

    private String defaultIfEmptyOrNull(String source, String defaultStr) {
        if ("null".equals(source)) {
            return defaultStr;
        }
        return defaultIfEmpty(source, defaultStr);
    }

    @Override
    public final LazyDataModel<Chart> getLazyDataModel() {
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
        if (trendList != null && trendList.size() >= 10) {
            BigDecimal firstAverage = computeAverage(trendList.subList(0, 9));
            BigDecimal lastAverage = computeAverage(trendList.subList(9, trendList.size()));
            BigDecimal averageTrend = (firstAverage == null || firstAverage.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO
                    : lastAverage.divide(firstAverage, 15, RoundingMode.HALF_UP);
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

    private ChartJsModel initChartJsModel(String mqCode) {
        ChartJsModel jsModel = new ChartJsModel();
        MeasurableQuantity mq = mqService.findByCode(mqCode);

        if (mq != null) {
            String dimension1 = mq.getDimension1();
            String dimension2 = mq.getDimension2();
            String dimension3 = mq.getDimension3();
            String dimension4 = mq.getDimension4();
            jsModel.getDimensions().put(DIMENSION_1, "null".equals(dimension1) ? null : dimension1);
            jsModel.getDimensions().put(DIMENSION_2, "null".equals(dimension2) ? null : dimension2);
            jsModel.getDimensions().put(DIMENSION_3, "null".equals(dimension3) ? null : dimension3);
            jsModel.getDimensions().put(DIMENSION_4, "null".equals(dimension4) ? null : dimension4);
            jsModel.setTitle(mq.getDescription());
        }

        jsModel.getDatasets().put(DIMENSION_1, new ArrayList<BigDecimal>());
        jsModel.getDatasets().put(DIMENSION_2, new ArrayList<BigDecimal>());
        jsModel.getDatasets().put(DIMENSION_3, new ArrayList<BigDecimal>());
        jsModel.getDatasets().put(DIMENSION_4, new ArrayList<BigDecimal>());
        jsModel.getDatasets().put(OTHERS, new ArrayList<BigDecimal>());
        jsModel.getDatasets().put(TREND, new ArrayList<BigDecimal>());
        jsModel.getDatasets().put(TOTAL, new ArrayList<BigDecimal>());

        return jsModel;
    }
}
