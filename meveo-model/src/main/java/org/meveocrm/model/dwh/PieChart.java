package org.meveocrm.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DWH_CHART_PIE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_CHART_PIE_SEQ")
public class PieChart extends Chart {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3549868233998052477L;

	@Column(name = "FILLED")
	boolean filled;

	@Enumerated(EnumType.STRING)
	@Column(name = "LEGEND_POSITION")
	LegendPositionEnum legendPosition;

	@Column(name = "SERIES_COLORS", length = 1000)
	String seriesColors;

	@Column(name = "DIAMETER")
	Integer diameter;

	@Column(name = "SLICE_MARGIN")
	int sliceMargin;

	@Column(name = "SHADOW")
	boolean shadow = true;

	@Column(name = "SHOW_DATA_LABELS")
	boolean showDataLabels;

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

}
