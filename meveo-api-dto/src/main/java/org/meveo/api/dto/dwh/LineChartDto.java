package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.LineChart;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "LineChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class LineChartDto extends ChartDto {

	private static final long serialVersionUID = -2876004902441563987L;

	private boolean filled;
	private String legendPosition;
	private String seriesColors = "1b788f";
	private boolean shadow = true;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private boolean breakOnNull;
	private String xaxisLabel;
	private String yaxisLabel;
	private Integer xaxisAngle;
	private Integer yaxisAngle;
	// Whether to stack series
	private boolean stacked;
	private boolean zoom;
	private boolean animate;
	private boolean showDataTip = true;
	private String datatipFormat;
	private int legendCols;
	private int legendRows;

	public LineChartDto() {

	}

	public LineChartDto(LineChart e) {
		super((Chart) e);
		filled = e.isFilled();
		if (e.getLegendPosition() != null) {
			legendPosition = e.getLegendPosition().name();
		}
		seriesColors = e.getSeriesColors();
		shadow = e.isShadow();
		minX = e.getMinX();
		minY = e.getMinY();
		maxX = e.getMaxX();
		maxY = e.getMaxY();
		breakOnNull = e.isBreakOnNull();
		xaxisLabel = e.getXaxisLabel();
		yaxisLabel = e.getYaxisLabel();
		xaxisAngle = e.getXaxisAngle();
		yaxisAngle = e.getYaxisAngle();
		stacked = e.isStacked();
		zoom = e.isZoom();
		animate = e.isAnimate();
		showDataTip = e.isShowDataTip();
		datatipFormat = e.getDatatipFormat();
		legendCols = e.getLegendCols();
		legendRows = e.getLegendRows();
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public String getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(String legendPosition) {
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
