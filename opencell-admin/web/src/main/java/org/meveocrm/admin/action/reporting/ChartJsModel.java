package org.meveocrm.admin.action.reporting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This model is used to contain data to charts created using the chart.js library.
 * see http://www.chartjs.org/docs/ for details on the chart.js library
 * 
 * @author Youssef IZEM
 * @lastModifiedVersion 5.2
 */
public class ChartJsModel {

    private List<String> chartLabels;
    private List<String> legendLabels;
    private Map<String, List<BigDecimal>> datasets;
    private Map<String, String> dimensions;
    private BigDecimal trendValue;
    private String title;

    public List<String> getChartLabels() {
        if (chartLabels == null) {
            chartLabels = new ArrayList<>();
        }
        return chartLabels;
    }

    public void setChartLabels(List<String> chartLabels) {
        this.chartLabels = chartLabels;
    }

    public List<String> getLegendLabels() {
        if (legendLabels == null){
            legendLabels = new ArrayList<>();
        }
        return legendLabels;
    }

    public void setLegendLabels(List<String> legendLabels) {
        this.legendLabels = legendLabels;
    }

    public Map<String, List<BigDecimal>> getDatasets() {
        if (datasets == null) {
            datasets = new HashMap<>();
        }
        return datasets;
    }

    public void setDatasets(Map<String, List<BigDecimal>> datasets) {
        this.datasets = datasets;
    }

    public Map<String, String> getDimensions() {
        if (dimensions == null) {
            dimensions = new HashMap<>();
        }
        return dimensions;
    }

    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
    }

    public BigDecimal getTrendValue() {
        return trendValue;
    }

    public void setTrendValue(BigDecimal trendValue) {
        this.trendValue = trendValue;
    }

    public String getTitle() {
        return title;
    }
    public String gettitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
