/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * Pie type chart
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "dwh_chart_pie")
@Deprecated
public class PieChart extends Chart {

    private static final long serialVersionUID = -3549868233998052477L;

    /**
     * Is it filled
     */
    @Type(type = "numeric_boolean")
    @Column(name = "filled")
    private boolean filled;

    /**
     * Legend position
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "legend_position")
    private LegendPositionEnum legendPosition;

    /**
     * Series colors
     */
    @Column(name = "series_colors", length = 1000)
    @Size(max = 1000)
    private String seriesColors = "1b788f";

    /**
     * Diameter
     */
    @Column(name = "diameter")
    private Integer diameter;

    /**
     * Slice margin
     */
    @Column(name = "slice_margin")
    private int sliceMargin;

    /**
     * Show shadow
     */
    @Type(type = "numeric_boolean")
    @Column(name = "shadow")
    private boolean shadow = true;

    /**
     * Show data labels
     */
    @Type(type = "numeric_boolean")
    @Column(name = "show_data_labels")
    private boolean showDataLabels;

    /**
     * Number of legend columns
     */
    @Column(name = "legend_cols")
    private int legendCols;

    /**
     * Number of legend rows
     */
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
