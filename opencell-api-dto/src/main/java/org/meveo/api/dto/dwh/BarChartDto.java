package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dwh.BarChart;
import org.meveo.model.dwh.LegendPositionEnum;
import org.meveo.model.dwh.OrientationEnum;

/**
 * The Class BarChartDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BarChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarChartDto extends ChartDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3708601896002824344L;

    /** The legend position. */
    private LegendPositionEnum legendPosition;

    /** The bar padding. */
    private int barPadding = 8;

    /** The bar margin. */
    private int barMargin = 10;

    /** The orientation. */
    private OrientationEnum orientation;

    /** Enables stacked display of bars. */
    private boolean stacked;

    /** Minimum boundary value. */
    private Double min;

    /** Minimum boundary value. */
    private Double max;

    /**
     * Whether line segments should be broken at null value, fall will join point on either side of line.
     */
    boolean breakOnNull;

    /** The xaxis label. */
    private String xaxisLabel;

    /** The yaxis label. */
    private String yaxisLabel;

    /** Angle of the x-axis ticks. */
    private Integer xaxisAngle;

    /** The yaxis angle. */
    private Integer yaxisAngle;

    /** The legend cols. */
    private int legendCols;

    /** The legend rows. */
    private int legendRows;

    /** Enables plot zooming. */
    private boolean zoom;

    /** Enables animation on plot rendering. */
    private boolean animate;

    /** Defines visibility of datatip. */
    private boolean showDataTip = true;

    /**
     * Template string for datatips.
     */
    private String datatipFormat;

    /**
     * Instantiates a new bar chart dto.
     */
    public BarChartDto() {
        super();
    }

    /**
     * Instantiates a new bar chart dto.
     *
     * @param chart the BarChart entity
     */
    public BarChartDto(BarChart chart) {
        super(chart);
        setLegendPosition(chart.getLegendPosition());
        setBarPadding(chart.getBarPadding());
        setBarMargin(chart.getBarMargin());
        setOrientation(chart.getOrientation());
        setStacked(chart.isStacked());
        setMin(chart.getMin());
        setMax(chart.getMax());
        setBreakOnNull(chart.isBreakOnNull());
        setXaxisLabel(chart.getXaxisLabel());
        setYaxisLabel(chart.getYaxisLabel());
        setXaxisAngle(chart.getXaxisAngle());
        setYaxisAngle(chart.getYaxisAngle());
        setLegendCols(chart.getLegendCols());
        setLegendRows(chart.getLegendRows());
        setZoom(chart.isZoom());
        setAnimate(chart.isAnimate());
        setShowDataTip(chart.isShowDataTip());
        setShowDataTip(chart.isShowDataTip());
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
     * Gets the bar padding.
     *
     * @return the bar padding
     */
    public int getBarPadding() {
        return barPadding;
    }

    /**
     * Sets the bar padding.
     *
     * @param barPadding the new bar padding
     */
    public void setBarPadding(int barPadding) {
        this.barPadding = barPadding;
    }

    /**
     * Gets the bar margin.
     *
     * @return the bar margin
     */
    public int getBarMargin() {
        return barMargin;
    }

    /**
     * Sets the bar margin.
     *
     * @param barMargin the new bar margin
     */
    public void setBarMargin(int barMargin) {
        this.barMargin = barMargin;
    }

    /**
     * Gets the orientation.
     *
     * @return the orientation
     */
    public OrientationEnum getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation.
     *
     * @param orientation the new orientation
     */
    public void setOrientation(OrientationEnum orientation) {
        this.orientation = orientation;
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
     * Gets the min.
     *
     * @return the min
     */
    public Double getMin() {
        return min;
    }

    /**
     * Sets the min.
     *
     * @param min the new min
     */
    public void setMin(Double min) {
        this.min = min;
    }

    /**
     * Gets the max.
     *
     * @return the max
     */
    public Double getMax() {
        return max;
    }

    /**
     * Sets the max.
     *
     * @param max the new max
     */
    public void setMax(Double max) {
        this.max = max;
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
     * Gets the serialversionuid.
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return String.format(
            "BarChartDto [%s,legendPosition=%s, barPadding=%s, barMargin=%s, orientation=%s, stacked=%s, min=%s, max=%s, breakOnNull=%s, xaxisLabel=%s, yaxisLabel=%s, xaxisAngle=%s, yaxisAngle=%s, legendCols=%s, legendRows=%s, zoom=%s, animate=%s, showDataTip=%s, datatipFormat=%s]",
            super.toString(), legendPosition, barPadding, barMargin, orientation, stacked, min, max, breakOnNull, xaxisLabel, yaxisLabel, xaxisAngle, yaxisAngle, legendCols,
            legendRows, zoom, animate, showDataTip, datatipFormat);
    }
}