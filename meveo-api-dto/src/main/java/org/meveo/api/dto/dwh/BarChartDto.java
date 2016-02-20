package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.Chart;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BarChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarChartDto extends ChartDto {

	private static final long serialVersionUID = 7595622432302931522L;

	private String legendPosition;
	private int barPadding = 8;
	private int barMargin = 10;
	private String orientation;
	private boolean stacked;
	private Double min;
	private Double max;
	private boolean breakOnNull;
	private String xaxisLabel;
	private String yaxisLabel;
	private Integer xaxisAngle;
	private Integer yaxisAngle;
	private int legendCols;
	private int legendRows;
	private boolean zoom;
	private boolean animate;
	private boolean showDataTip = true;
	private String datatipFormat;

	public BarChartDto() {

	}

	public BarChartDto(BarChart e) {
		super((Chart) e);
		if (e.getLegendPosition() != null) {
			legendPosition = e.getLegendPosition().name();
		}
		barPadding = e.getBarPadding();
		barMargin = e.getBarMargin();
		if (e.getOrientation() != null) {
			orientation = e.getOrientation().name();
		}
		stacked = e.isStacked();
		min = e.getMin();
		max = e.getMax();
		breakOnNull = e.isBreakOnNull();
		xaxisLabel = e.getXaxisLabel();
		yaxisLabel = e.getYaxisLabel();
		xaxisAngle = e.getXaxisAngle();
		yaxisAngle = e.getYaxisAngle();
		legendCols = e.getLegendCols();
		legendRows = e.getLegendRows();
		zoom = e.isZoom();
		animate = e.isAnimate();
		showDataTip = e.isShowDataTip();
		datatipFormat = e.getDatatipFormat();
	}

	public String getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(String legendPosition) {
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

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
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
