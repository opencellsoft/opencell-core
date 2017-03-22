package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dwh.LegendPositionEnum;
import org.meveo.model.dwh.PieChart;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "PieChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class PieChartDto extends ChartDto {

    private static final long serialVersionUID = -5954424187693917178L;

    private boolean filled;
    private LegendPositionEnum legendPosition;
    private String seriesColors = "1b788f";
    private Integer diameter;
    private int sliceMargin;
    private boolean shadow = true;
    private boolean showDataLabels;
    private int legendCols;
    private int legendRows;

    public PieChartDto() {
        super();
    }

    public PieChartDto(PieChart chart) {
        super(chart);
        setFilled(chart.isFilled());
        setLegendPosition(chart.getLegendPosition());
        setSeriesColors(chart.getSeriesColors());
        setDiameter(chart.getDiameter());
        setSliceMargin(chart.getSliceMargin());
        setShadow(chart.isShadow());
        setShadow(chart.isShowDataLabels());
        setLegendCols(chart.getLegendCols());
        setLegendRows(chart.getLegendRows());
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public LegendPositionEnum getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(LegendPositionEnum legendPosition) {
        this.legendPosition = legendPosition;
    }

    public String getSeriesColors() {
        return seriesColors;
    }

    public void setSeriesColors(String seriesColors) {
        this.seriesColors = seriesColors;
    }

    public Integer getDiameter() {
        return diameter;
    }

    public void setDiameter(Integer diameter) {
        this.diameter = diameter;
    }

    public int getSliceMargin() {
        return sliceMargin;
    }

    public void setSliceMargin(int sliceMargin) {
        this.sliceMargin = sliceMargin;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public boolean isShowDataLabels() {
        return showDataLabels;
    }

    public void setShowDataLabels(boolean showDataLabels) {
        this.showDataLabels = showDataLabels;
    }

    public int getLegendCols() {
        return legendCols;
    }

    public void setLegendCols(int legendCols) {
        this.legendCols = legendCols;
    }

    public int getLegendRows() {
        return legendRows;
    }

    public void setLegendRows(int legendRows) {
        this.legendRows = legendRows;
    }

    @Override
    public String toString() {
        return String.format(
            "PieChartDto [%s, filled=%s, legendPosition=%s, seriesColors=%s, diameter=%s, sliceMargin=%s, shadow=%s, showDataLabels=%s, legendCols=%s, legendRows=%s]",
            super.toString(), filled, legendPosition, seriesColors, diameter, sliceMargin, shadow, showDataLabels, legendCols, legendRows);
    }
}