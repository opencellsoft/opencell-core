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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

/**
 * Bar type chart
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "dwh_chart_bar")
@Deprecated
public class BarChart extends Chart {

    private static final long serialVersionUID = -3247705449113663454L;

    /**
     * Legend position
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "legend_position")
    private LegendPositionEnum legendPosition;

    /**
     * Bar padding
     */
    @Column(name = "barpadding")
    @NotNull
    private int barPadding = 8;

    /**
     * Bar margin
     */
    @Column(name = "barmargin")
    @NotNull
    private int barMargin = 10;

    /**
     * Bar orientation
     */
    @Column(name = "orientation")
    @Enumerated(EnumType.ORDINAL)
    private OrientationEnum orientation;

    /**
     * Enables stacked display of bars
     */
    @Type(type = "numeric_boolean")
    @Column(name = "stacked")
    private boolean stacked;

    /**
     * Minimum boundary value
     */
    @Column(name = "min")
    private Double min;

    /**
     * Minimum boundary value
     */
    @Column(name = "max")
    private Double max;

    /**
     * Whether line segments should be broken at null value, fall will join point on either side of line.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "break_on_null")
    private boolean breakOnNull;

    /**
     * X-axis label
     */
    @Column(name = "x_axis_label", length = 255)
    @Size(max = 255)
    private String xaxisLabel;

    /**
     * Y-axis label
     */
    @Column(name = "y_axis_label", length = 255)
    @Size(max = 255)
    private String yaxisLabel;

    /**
     * Angle of the x-axis ticks
     */
    @Column(name = "x_axis_angle")
    private Integer xaxisAngle;

    /**
     * Angle of y-axis ticks
     */
    @Column(name = "y_axis_angle")
    private Integer yaxisAngle;

    /**
     * Legend number of columns
     */
    @Column(name = "legend_cols")
    private int legendCols;

    /**
     * Legend number of rows
     */
    @Column(name = "legend_rows")
    private int legendRows;

    /**
     * Enables plot zooming
     */
    @Type(type = "numeric_boolean")
    @Column(name = "zoom")
    private boolean zoom;

    /**
     * Enables animation on plot rendering
     */
    @Type(type = "numeric_boolean")
    @Column(name = "animate")
    private boolean animate;

    /**
     * Defines visibility of datatip
     */
    @Type(type = "numeric_boolean")
    @Column(name = "show_data_tip")
    private boolean showDataTip = true;

    /**
     * Template string for datatips
     */
    @Column(name = "data_tip_format", length = 255)
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
