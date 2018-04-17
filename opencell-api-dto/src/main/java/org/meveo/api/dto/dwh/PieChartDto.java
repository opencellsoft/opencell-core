package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dwh.LegendPositionEnum;
import org.meveo.model.dwh.PieChart;

/**
 * The Class PieChartDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "PieChart")
@XmlAccessorType(XmlAccessType.FIELD)
public class PieChartDto extends ChartDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5954424187693917178L;

    /** The filled. */
    private boolean filled;
    
    /** The legend position. */
    private LegendPositionEnum legendPosition;
    
    /** The series colors. */
    private String seriesColors = "1b788f";
    
    /** The diameter. */
    private Integer diameter;
    
    /** The slice margin. */
    private int sliceMargin;
    
    /** The shadow. */
    private boolean shadow = true;
    
    /** The show data labels. */
    private boolean showDataLabels;
    
    /** The legend cols. */
    private int legendCols;
    
    /** The legend rows. */
    private int legendRows;

    /**
     * Instantiates a new pie chart dto.
     */
    public PieChartDto() {
        super();
    }

    /**
     * Instantiates a new pie chart dto.
     *
     * @param chart the PieChart entity
     */
    public PieChartDto(PieChart chart) {
        super(chart);
        setFilled(chart.isFilled());
        setLegendPosition(chart.getLegendPosition());
        setSeriesColors(chart.getSeriesColors());
        setDiameter(chart.getDiameter());
        setSliceMargin(chart.getSliceMargin());
        setShadow(chart.isShadow());
        setShadow(chart.isShowDataLabels());
        setLegendCols(chart.getLegendCols());
        setLegendRows(chart.getLegendRows());
    }

    /**
     * Checks if is filled.
     *
     * @return true, if is filled
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Sets the filled.
     *
     * @param filled the new filled
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
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
     * Gets the series colors.
     *
     * @return the series colors
     */
    public String getSeriesColors() {
        return seriesColors;
    }

    /**
     * Sets the series colors.
     *
     * @param seriesColors the new series colors
     */
    public void setSeriesColors(String seriesColors) {
        this.seriesColors = seriesColors;
    }

    /**
     * Gets the diameter.
     *
     * @return the diameter
     */
    public Integer getDiameter() {
        return diameter;
    }

    /**
     * Sets the diameter.
     *
     * @param diameter the new diameter
     */
    public void setDiameter(Integer diameter) {
        this.diameter = diameter;
    }

    /**
     * Gets the slice margin.
     *
     * @return the slice margin
     */
    public int getSliceMargin() {
        return sliceMargin;
    }

    /**
     * Sets the slice margin.
     *
     * @param sliceMargin the new slice margin
     */
    public void setSliceMargin(int sliceMargin) {
        this.sliceMargin = sliceMargin;
    }

    /**
     * Checks if is shadow.
     *
     * @return true, if is shadow
     */
    public boolean isShadow() {
        return shadow;
    }

    /**
     * Sets the shadow.
     *
     * @param shadow the new shadow
     */
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    /**
     * Checks if is show data labels.
     *
     * @return true, if is show data labels
     */
    public boolean isShowDataLabels() {
        return showDataLabels;
    }

    /**
     * Sets the show data labels.
     *
     * @param showDataLabels the new show data labels
     */
    public void setShowDataLabels(boolean showDataLabels) {
        this.showDataLabels = showDataLabels;
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

    @Override
    public String toString() {
        return String.format(
            "PieChartDto [%s, filled=%s, legendPosition=%s, seriesColors=%s, diameter=%s, sliceMargin=%s, shadow=%s, showDataLabels=%s, legendCols=%s, legendRows=%s]",
            super.toString(), filled, legendPosition, seriesColors, diameter, sliceMargin, shadow, showDataLabels, legendCols, legendRows);
    }
}