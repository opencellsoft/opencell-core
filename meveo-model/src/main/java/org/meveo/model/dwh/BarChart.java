package org.meveo.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "DWH_CHART_BAR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_CHART_BAR_SEQ")
public class BarChart extends Chart {

	private static final long serialVersionUID = -3247705449113663454L;

	@Enumerated(EnumType.STRING)
	@Column(name="LEGEND_POSITION")
	private LegendPositionEnum legendPosition;
	

	@Column(name="BARPADDING")
	@NotNull
	private int barPadding = 8;

	@Column(name="BARMARGIN")
    @NotNull
	private int barMargin = 10;

	@Column(name="ORIENTATION")
	private OrientationEnum orientation;

	//Enables stacked display of bars
	@Column(name="STACKED")
	private boolean stacked;
	
	//Minimum boundary value.
	@Column(name="MIN")
	private Double min;
	
	//Minimum boundary value.
	@Column(name="MAX")
	private Double max;

	//Whether line segments should be broken at null
	//value, fall will join point on either side of line.
	@Column(name="BREAK_ON_NULL")
	private boolean breakOnNull;


	@Column(name="X_AXIS_LABEL", length = 255)
	@Size(max = 255)
	private String xaxisLabel;

	@Column(name="Y_AXIS_LABEL", length = 255)
    @Size(max = 255)
	private String yaxisLabel;
	
	//Angle of the x-axis ticks
	@Column(name="X_AXIS_ANGLE")
	private Integer xaxisAngle;

	@Column(name="Y_AXIS_ANGLE")
	private Integer yaxisAngle;
	

	@Column(name="LEGEND_COLS")
	private int legendCols;

	@Column(name="LEGEND_ROWS")
	private int legendRows;

	//Enables plot zooming.
	@Column(name="ZOOM")
	private boolean zoom;
	
	//Enables animation on plot rendering
	@Column(name="ANIMATE")
	private boolean animate;
	
	//Defines visibility of datatip.
	@Column(name="SHOW_DATA_TIP")
	private boolean showDataTip=true;
	
	//Template string for datatips.
	@Column(name="DATA_TIP_FORMAT", length = 255)
    @Size(max = 255)
	private String datatipFormat;

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

}
