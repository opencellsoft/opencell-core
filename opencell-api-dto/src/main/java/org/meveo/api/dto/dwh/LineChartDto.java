package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dwh.LegendPositionEnum;
import org.meveo.model.dwh.LineChart;

/**
 * The Class LineChartDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "LineChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class LineChartDto extends ChartDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 991040239718953294L;

    /** The filled. */
    private boolean filled;

    /** The legend position. */
    private LegendPositionEnum legendPosition;

    /** The series colors. */
    private String seriesColors = "1b788f";

    /** The shadow. */
    private boolean shadow = true;

    /** The min X. */
    private int minX;

    /** The max X. */
    private int maxX;

    /** The min Y. */
    private int minY;

    /** The max Y. */
    private int maxY;

    /**
     * Whether line segments should be broken at null value, fall will join point on either side of line.
     */
    private boolean breakOnNull;

    /** The xaxis label. */
    private String xaxisLabel;

    /** The yaxis label. */
    private String yaxisLabel;

    /** Angle of the x-axis ticks. */
    private Integer xaxisAngle;

    /** The yaxis angle. */
    private Integer yaxisAngle;

    /** Whether to stack series. */
    private boolean stacked;

    /** Enables plot zooming. */
    private boolean zoom;

    /** Enables animation on plot rendering. */
    private boolean animate;

    /** Defines visibility of datatip. */
    private boolean showDataTip = true;

    /** Template string for datatips. */
    private String datatipFormat;

    /** The legend cols. */
    private int legendCols;

    /** The legend rows. */
    private int legendRows;

    /**
     * Instantiates a new line chart dto.
     */
    public LineChartDto() {
        super();
    }

    /**
     * Instantiates a new line chart dto.
     *
     * @param chart the LineChart entity
     */
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

    /**
     * Checks if is filled.
     *
     * @return true, if is filled
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Sets the filled.
     *
     * @param filled the new filled
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    /**
     * Gets the legend position.
     *
     * @return the legend position
     */
    public LegendPositionEnum getLegendPosition() {
        return legendPosition;
    }

    /**
     * Sets the legend position.
     *
     * @param legendPosition the new legend position
     */
    public void setLegendPosition(LegendPositionEnum legendPosition) {
        this.legendPosition = legendPosition;
    }

    /**
     * Gets the series colors.
     *
     * @return the series colors
     */
    public String getSeriesColors() {
        return seriesColors;
    }

    /**
     * Sets the series colors.
     *
     * @param seriesColors the new series colors
     */
    public void setSeriesColors(String seriesColors) {
        this.seriesColors = seriesColors;
    }

    /**
     * Checks if is shadow.
     *
     * @return true, if is shadow
     */
    public boolean isShadow() {
        return shadow;
    }

    /**
     * Sets the shadow.
     *
     * @param shadow the new shadow
     */
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    /**
     * Gets the min X.
     *
     * @return the min X
     */
    public int getMinX() {
        return minX;
    }

    /**
     * Sets the min X.
     *
     * @param minX the new min X
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * Gets the max X.
     *
     * @return the max X
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * Sets the max X.
     *
     * @param maxX the new max X
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    /**
     * Gets the min Y.
     *
     * @return the min Y
     */
    public int getMinY() {
        return minY;
    }

    /**
     * Sets the min Y.
     *
     * @param minY the new min Y
     */
    public void setMinY(int minY) {
        this.minY = minY;
    }

    /**
     * Gets the max Y.
     *
     * @return the max Y
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * Sets the max Y.
     *
     * @param maxY the new max Y
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    /**
     * Checks if is break on null.
     *
     * @return true, if is break on null
     */
    public boolean isBreakOnNull() {
        return breakOnNull;
    }

    /**
     * Sets the break on null.
     *
     * @param breakOnNull the new break on null
     */
    public void setBreakOnNull(boolean breakOnNull) {
        this.breakOnNull = breakOnNull;
    }

    /**
     * Gets the xaxis label.
     *
     * @return the xaxis label
     */
    public String getXaxisLabel() {
        return xaxisLabel;
    }

    /**
     * Sets the xaxis label.
     *
     * @param xaxisLabel the new xaxis label
     */
    public void setXaxisLabel(String xaxisLabel) {
        this.xaxisLabel = xaxisLabel;
    }

    /**
     * Gets the yaxis label.
     *
     * @return the yaxis label
     */
    public String getYaxisLabel() {
        return yaxisLabel;
    }

    /**
     * Sets the yaxis label.
     *
     * @param yaxisLabel the new yaxis label
     */
    public void setYaxisLabel(String yaxisLabel) {
        this.yaxisLabel = yaxisLabel;
    }

    /**
     * Gets the xaxis angle.
     *
     * @return the xaxis angle
     */
    public Integer getXaxisAngle() {
        return xaxisAngle;
    }

    /**
     * Sets the xaxis angle.
     *
     * @param xaxisAngle the new xaxis angle
     */
    public void setXaxisAngle(Integer xaxisAngle) {
        this.xaxisAngle = xaxisAngle;
    }

    /**
     * Gets the yaxis angle.
     *
     * @return the yaxis angle
     */
    public Integer getYaxisAngle() {
        return yaxisAngle;
    }

    /**
     * Sets the yaxis angle.
     *
     * @param yaxisAngle the new yaxis angle
     */
    public void setYaxisAngle(Integer yaxisAngle) {
        this.yaxisAngle = yaxisAngle;
    }

    /**
     * Checks if is stacked.
     *
     * @return true, if is stacked
     */
    public boolean isStacked() {
        return stacked;
    }

    /**
     * Sets the stacked.
     *
     * @param stacked the new stacked
     */
    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    /**
     * Checks if is zoom.
     *
     * @return true, if is zoom
     */
    public boolean isZoom() {
        return zoom;
    }

    /**
     * Sets the zoom.
     *
     * @param zoom the new zoom
     */
    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    /**
     * Checks if is animate.
     *
     * @return true, if is animate
     */
    public boolean isAnimate() {
        return animate;
    }

    /**
     * Sets the animate.
     *
     * @param animate the new animate
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    /**
     * Checks if is show data tip.
     *
     * @return true, if is show data tip
     */
    public boolean isShowDataTip() {
        return showDataTip;
    }

    /**
     * Sets the show data tip.
     *
     * @param showDataTip the new show data tip
     */
    public void setShowDataTip(boolean showDataTip) {
        this.showDataTip = showDataTip;
    }

    /**
     * Gets the datatip format.
     *
     * @return the datatip format
     */
    public String getDatatipFormat() {
        return datatipFormat;
    }

    /**
     * Sets the datatip format.
     *
     * @param datatipFormat the new datatip format
     */
    public void setDatatipFormat(String datatipFormat) {
        this.datatipFormat = datatipFormat;
    }

    /**
     * Gets the legend cols.
     *
     * @return the legend cols
     */
    public int getLegendCols() {
        return legendCols;
    }

    /**
     * Sets the legend cols.
     *
     * @param legendCols the new legend cols
     */
    public void setLegendCols(int legendCols) {
        this.legendCols = legendCols;
    }

    /**
     * Gets the legend rows.
     *
     * @return the legend rows
     */
    public int getLegendRows() {
        return legendRows;
    }

    /**
     * Sets the legend rows.
     *
     * @param legendRows the new legend rows
     */
    public void setLegendRows(int legendRows) {
        this.legendRows = legendRows;
    }

    @Override
    public String toString() {
        return String.format(
            "LineChartDto [%s, filled=%s, legendPosition=%s, seriesColors=%s, shadow=%s, minX=%s, maxX=%s, minY=%s, maxY=%s, breakOnNull=%s, xaxisLabel=%s, yaxisLabel=%s, xaxisAngle=%s, yaxisAngle=%s, stacked=%s, zoom=%s, animate=%s, showDataTip=%s, datatipFormat=%s, legendCols=%s, legendRows=%s]",
            super.toString(), filled, legendPosition, seriesColors, shadow, minX, maxX, minY, maxY, breakOnNull, xaxisLabel, yaxisLabel, xaxisAngle, yaxisAngle, stacked, zoom,
            animate, showDataTip, datatipFormat, legendCols, legendRows);
    }
}