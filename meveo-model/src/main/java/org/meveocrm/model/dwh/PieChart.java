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

	@Column(name="FILLED")
	boolean filled;

	@Enumerated(EnumType.STRING)
	@Column(name="LEGEND_POSITION")
	LegendPositionEnum legendPosition;

	@Column(name="SERIES_COLORS",length=1000)
	String seriesColors;

	@Column(name="DIAMETER")
	Integer diameter;

	@Column(name="SLICE_MARGIN")
	int sliceMargin;

	@Column(name="SHADOW")
	boolean shadow=true;

	@Column(name="SHOW_DATA_LABELS")
	boolean showDataLabels;

	@Column(name="LEGEND_COLS")
	int legendCols;

	@Column(name="LEGEND_ROWS")
	int legendRows;	
	
}
