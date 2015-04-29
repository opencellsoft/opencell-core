package org.meveocrm.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DWH_CHART_LINE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_CHART_LINE_SEQ")
public class LineChart extends Chart {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1563273820297215070L;

	@Column(name = "FILLED")
	boolean filled;

	@Enumerated(EnumType.STRING)
	@Column(name = "LEGEND_POSITION")
	LegendPositionEnum legendPosition;

	@Column(name = "SERIES_COLORS", length = 1000)
	String seriesColors = "1b788f";

	@Column(name = "SHADOW")
	boolean shadow = true;

	@Column(name = "MIN_X")
	int minX;

	@Column(name = "MAX_X")
	int maxX;

	@Column(name = "MIN_Y")
	int minY;

	@Column(name = "MAX_Y")
	int maxY;

	// Whether line segments should be broken at null
	// value, fall will join point on either side of line.
	@Column(name = "BREAK_ON_NULL")
	boolean breakOnNull;

	@Column(name = "X_AXIS_LABEL")
	String xaxisLabel;

	@Column(name = "Y_AXIS_LABEL")
	String yaxisLabel;

	// Angle of the x-axis ticks
	@Column(name = "X_AXIS_ANGLE")
	Integer xaxisAngle;

	@Column(name = "Y_AXIS_ANGLE")
	Integer yaxisAngle;

	// Whether to stack series
	@Column(name = "STACKED")
	boolean stacked;

	// Enables plot zooming.
	@Column(name = "ZOOM")
	boolean zoom;

	// Enables animation on plot rendering
	@Column(name = "ANIMATE")
	boolean animate;

	// Defines visibility of datatip.
	@Column(name = "SHOW_DATA_TIP")
	boolean showDataTip = true;

	// Template string for datatips.
	@Column(name = "DATA_TIP_FORMAT")
	String datatipFormat;

	@Column(name = "LEGEND_COLS")
	int legendCols;

	@Column(name = "LEGEND_ROWS")
	int legendRows;

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

}
