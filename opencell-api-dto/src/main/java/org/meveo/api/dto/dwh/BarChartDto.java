package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dwh.BarChart;
import org.meveo.model.dwh.LegendPositionEnum;
import org.meveo.model.dwh.OrientationEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BarChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarChartDto extends ChartDto {

    private static final long serialVersionUID = -3708601896002824344L;

    private LegendPositionEnum legendPosition;

    private int barPadding = 8;

    private int barMargin = 10;

    private OrientationEnum orientation;

    /** Enables stacked display of bars */
    private boolean stacked;

    /** Minimum boundary value. */
    private Double min;

    /** Minimum boundary value. */
    private Double max;

    /**
     * Whether line segments should be broken at null value, fall will join point on either side of line.
     */
    boolean breakOnNull;

    private String xaxisLabel;

    private String yaxisLabel;

    /** Angle of the x-axis ticks */
    private Integer xaxisAngle;

    private Integer yaxisAngle;

    private int legendCols;

    private int legendRows;

    /** Enables plot zooming. */
    private boolean zoom;

    /** Enables animation on plot rendering */
    private boolean animate;

    /** Defines visibility of datatip. */
    private boolean showDataTip = true;

    /**
     * Template string for datatips.
     */
    private String datatipFormat;

    public BarChartDto() {
        super();
    }

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

    public LegendPositionEnum getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(LegendPositionEnum legendPosition) {
        this.legendPosition = legendPosition;
    }

    public int getBarPadding() {
        return barPadding;
    }

    public void setBarPadding(int barPadding) {
        this.barPadding = barPadding;
    }

    public int getBarMargin() {
        return barMargin;
    }

    public void setBarMargin(int barMargin) {
        this.barMargin = barMargin;
    }

    public OrientationEnum getOrientation() {
        return orientation;
    }

    public void setOrientation(OrientationEnum orientation) {
        this.orientation = orientation;
    }

    public boolean isStacked() {
        return stacked;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return String
            .format(
                "BarChartDto [%s,legendPosition=%s, barPadding=%s, barMargin=%s, orientation=%s, stacked=%s, min=%s, max=%s, breakOnNull=%s, xaxisLabel=%s, yaxisLabel=%s, xaxisAngle=%s, yaxisAngle=%s, legendCols=%s, legendRows=%s, zoom=%s, animate=%s, showDataTip=%s, datatipFormat=%s]",
                super.toString(), legendPosition, barPadding, barMargin, orientation, stacked, min, max, breakOnNull, xaxisLabel, yaxisLabel, xaxisAngle, yaxisAngle, legendCols,
                legendRows, zoom, animate, showDataTip, datatipFormat);
    }
}