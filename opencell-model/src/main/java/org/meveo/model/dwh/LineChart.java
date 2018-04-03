package org.meveo.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "dwh_chart_line")
public class LineChart extends Chart {

	private static final long serialVersionUID = 1563273820297215070L;

	@Type(type="numeric_boolean")
    @Column(name = "filled")
	private boolean filled;

	@Enumerated(EnumType.STRING)
	@Column(name = "legend_position")
	private LegendPositionEnum legendPosition;

	@Column(name = "series_colors", length = 1000)
	@Size(max = 1000)
	private String seriesColors = "1b788f";

	@Type(type="numeric_boolean")
    @Column(name = "shadow")
	private boolean shadow = true;

	@Column(name = "min_x")
	private int minX;

	@Column(name = "max_x")
	private int maxX;

	@Column(name = "min_y")
	private int minY;

	@Column(name = "max_y")
	private int maxY;

	// Whether line segments should be broken at null
	// value, fall will join point on either side of line.
	@Type(type="numeric_boolean")
    @Column(name = "break_on_null")
	private boolean breakOnNull;

	@Column(name = "x_axis_label", length = 255)
	@Size(max = 255)
	private String xaxisLabel;

	@Column(name = "y_axis_label", length = 255)
    @Size(max = 255)
	private String yaxisLabel;

	// Angle of the x-axis ticks
	@Column(name = "x_axis_angle")
	private Integer xaxisAngle;

	@Column(name = "y_axis_angle")
	private Integer yaxisAngle;

	// Whether to stack series
	@Type(type="numeric_boolean")
    @Column(name = "stacked")
	private boolean stacked;

	// Enables plot zooming.
	@Type(type="numeric_boolean")
    @Column(name = "zoom")
	private boolean zoom;

	// Enables animation on plot rendering
	@Type(type="numeric_boolean")
    @Column(name = "animate")
	private boolean animate;

	// Defines visibility of datatip.
	@Type(type="numeric_boolean")
    @Column(name = "show_data_tip")
	private boolean showDataTip = true;

	// Template string for datatips.
	@Column(name = "data_tip_format", length = 255)
    @Size(max = 255)
	private String datatipFormat;

	@Column(name = "legend_cols")
	private int legendCols;

	@Column(name = "legend_rows")
	private int legendRows;

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
