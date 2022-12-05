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

import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Line type chart
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "dwh_chart_line")
public class LineChart extends Chart {

    private static final long serialVersionUID = 1563273820297215070L;

    /**
     * Is it filled
     */
    @Convert(converter = NumericBooleanConverter.class)
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
     * Show shadow
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "shadow")
    private boolean shadow = true;

    /**
     * Minimum X value
     */
    @Column(name = "min_x")
    private int minX;

    /**
     * Maximum X value
     */
    @Column(name = "max_x")
    private int maxX;

    /**
     * Minimum Y value
     */
    @Column(name = "min_y")
    private int minY;

    /**
     * Maximum Y value
     */
    @Column(name = "max_y")
    private int maxY;

    /**
     * Whether line segments should be broken at null value, fall will join point on either side of line.
     */
    @Convert(converter = NumericBooleanConverter.class)
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
     * Angle of the X-axis ticks
     */
    @Column(name = "x_axis_angle")
    private Integer xaxisAngle;

    /**
     * Angle of the Y-axis ticks
     */
    @Column(name = "y_axis_angle")
    private Integer yaxisAngle;

    /**
     * Whether to stack series
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "stacked")
    private boolean stacked;

    /**
     * Enables plot zooming.
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "zoom")
    private boolean zoom;

    /**
     * Enables animation on plot rendering
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "animate")
    private boolean animate;

    /**
     * Defines visibility of datatip.
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "show_data_tip")
    private boolean showDataTip = true;

    /**
     * Template string for datatips.
     */
    @Column(name = "data_tip_format", length = 255)
    @Size(max = 255)
    private String datatipFormat;

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
