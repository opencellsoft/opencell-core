package org.meveo.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "DWH_CHART_PIE")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "DWH_CHART_PIE_SEQ"), })
public class PieChart extends Chart {

    private static final long serialVersionUID = -3549868233998052477L;

    @Type(type="numeric_boolean")
    @Column(name = "FILLED")
    private boolean filled;

    @Enumerated(EnumType.STRING)
    @Column(name = "LEGEND_POSITION")
    private LegendPositionEnum legendPosition;

    @Column(name = "SERIES_COLORS", length = 1000)
    @Size(max = 1000)
    private String seriesColors = "1b788f";

    @Column(name = "DIAMETER")
    private Integer diameter;

    @Column(name = "SLICE_MARGIN")
    private int sliceMargin;

    @Type(type="numeric_boolean")
    @Column(name = "SHADOW")
    private boolean shadow = true;

    @Type(type="numeric_boolean")
    @Column(name = "SHOW_DATA_LABELS")
    private boolean showDataLabels;

    @Column(name = "LEGEND_COLS")
    private int legendCols;

    @Column(name = "LEGEND_ROWS")
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
