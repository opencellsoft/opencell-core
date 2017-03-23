package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dwh.LegendPositionEnum;
import org.meveo.model.dwh.LineChart;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "LineChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class LineChartDto extends ChartDto {

    private static final long serialVersionUID = 991040239718953294L;

    private boolean filled;

    private LegendPositionEnum legendPosition;

    private String seriesColors = "1b788f";

    private boolean shadow = true;

    private int minX;

    private int maxX;

    private int minY;

    private int maxY;

    /**
     * Whether line segments should be broken at null value, fall will join point on either side of line.
     */
    private boolean breakOnNull;

    private String xaxisLabel;

    private String yaxisLabel;

    /**
     * Angle of the x-axis ticks
     */
    private Integer xaxisAngle;

    private Integer yaxisAngle;

    /** Whether to stack series */
    private boolean stacked;

    /** Enables plot zooming. */
    private boolean zoom;

    /** Enables animation on plot rendering */
    private boolean animate;

    /** Defines visibility of datatip. */
    private boolean showDataTip = true;

    /** Template string for datatips. */
    private String datatipFormat;

    private int legendCols;

    private int legendRows;

    public LineChartDto() {
        super();
    }

    public LineChartDto(LineChart chart) {
        super(chart);
        setFilled(chart.isFilled());
        setLegendPosition(chart.getLegendPosition());
        setSeriesColors(chart.getSeriesColors());
        setShadow(chart.isShadow());
        setMinX(chart.getMinX());
        setMaxX(chart.getMaxX());
        setMinY(chart.getMinY());
        setMaxY(chart.getMaxY());
        setBreakOnNull(chart.isBreakOnNull());
        setXaxisLabel(chart.getXaxisLabel());
        setYaxisLabel(chart.getYaxisLabel());
        setXaxisAngle(chart.getXaxisAngle());
        setYaxisAngle(chart.getYaxisAngle());
        setStacked(chart.isStacked());
        setZoom(chart.isZoom());
        setAnimate(chart.isAnimate());
        setShowDataTip(chart.isShowDataTip());
        setDatatipFormat(chart.getDatatipFormat());
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

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public boolean isBreakOnNull() {
        return breakOnNull;
    }

    public void setBreakOnNull(boolean breakOnNull) {
        this.breakOnNull = breakOnNull;
    }

    public String getXaxisLabel() {
        return xaxisLabel;
    }

    public void setXaxisLabel(String xaxisLabel) {
        this.xaxisLabel = xaxisLabel;
    }

    public String getYaxisLabel() {
        return yaxisLabel;
    }

    public void setYaxisLabel(String yaxisLabel) {
        this.yaxisLabel = yaxisLabel;
    }

    public Integer getXaxisAngle() {
        return xaxisAngle;
    }

    public void setXaxisAngle(Integer xaxisAngle) {
        this.xaxisAngle = xaxisAngle;
    }

    public Integer getYaxisAngle() {
        return yaxisAngle;
    }

    public void setYaxisAngle(Integer yaxisAngle) {
        this.yaxisAngle = yaxisAngle;
    }

    public boolean isStacked() {
        return stacked;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public boolean isZoom() {
        return zoom;
    }

    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isShowDataTip() {
        return showDataTip;
    }

    public void setShowDataTip(boolean showDataTip) {
        this.showDataTip = showDataTip;
    }

    public String getDatatipFormat() {
        return datatipFormat;
    }

    public void setDatatipFormat(String datatipFormat) {
        this.datatipFormat = datatipFormat;
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
        return String
            .format(
                "LineChartDto [%s, filled=%s, legendPosition=%s, seriesColors=%s, shadow=%s, minX=%s, maxX=%s, minY=%s, maxY=%s, breakOnNull=%s, xaxisLabel=%s, yaxisLabel=%s, xaxisAngle=%s, yaxisAngle=%s, stacked=%s, zoom=%s, animate=%s, showDataTip=%s, datatipFormat=%s, legendCols=%s, legendRows=%s]",
                super.toString(), filled, legendPosition, seriesColors, shadow, minX, maxX, minY, maxY, breakOnNull, xaxisLabel, yaxisLabel, xaxisAngle, yaxisAngle, stacked, zoom,
                animate, showDataTip, datatipFormat, legendCols, legendRows);
    }
}