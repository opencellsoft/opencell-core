package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.dwh.Chart;

/**
 * The Class ChartDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Chart")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2573963792647472501L;

    /** The measurable quantity. */
    private MeasurableQuantityDto measurableQuantity;
    
    /** The width. */
    private String width = "500px";
    
    /** The height. */
    private String height = "300px";
    
    /** The style. */
    private String style;
    
    /** The style class. */
    private String styleClass;
    
    /** The extender. */
    private String extender;
    
    /** The visible. */
    private Boolean visible = false;

    /**
     * Instantiates a new chart dto.
     */
    public ChartDto() {
        super();
    }

    /**
     * Instantiates a new chart dto.
     *
     * @param chart the chart entity
     */
    public ChartDto(Chart chart) {
        super(chart);

        if (chart.getMeasurableQuantity() != null) {
            setMeasurableQuantity(new MeasurableQuantityDto(chart.getMeasurableQuantity()));
        }
        setWidth(chart.getWidth());
        setHeight(chart.getHeight());
        setStyle(chart.getStyle());
        setStyleClass(chart.getStyleClass());
        setExtender(chart.getExtender());
        setVisible(chart.isVisible());
    }

    /**
     * Gets the measurable quantity.
     *
     * @return the measurable quantity
     */
    public MeasurableQuantityDto getMeasurableQuantity() {
        return measurableQuantity;
    }

    /**
     * Sets the measurable quantity.
     *
     * @param measurableQuantity the new measurable quantity
     */
    public void setMeasurableQuantity(MeasurableQuantityDto measurableQuantity) {
        this.measurableQuantity = measurableQuantity;
    }

    /**
     * Gets the width.
     *
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the width.
     *
     * @param width the new width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Gets the height.
     *
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the height.
     *
     * @param height the new height
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Gets the style.
     *
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the style.
     *
     * @param style the new style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Gets the style class.
     *
     * @return the style class
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Sets the style class.
     *
     * @param styleClass the new style class
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Gets the extender.
     *
     * @return the extender
     */
    public String getExtender() {
        return extender;
    }

    /**
     * Sets the extender.
     *
     * @param extender the new extender
     */
    public void setExtender(String extender) {
        this.extender = extender;
    }

    /**
     * Gets the visible.
     *
     * @return the visible
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * Sets the visible.
     *
     * @param visible the new visible
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return String.format("ChartDto [code=%s, description=%s, measurableQuantityDto=%s, width=%s, height=%s, style=%s, styleClass=%s, extender=%s, visible=%s]", getCode(),
            getDescription(), measurableQuantity, width, height, style, styleClass, extender, visible);
    }
}