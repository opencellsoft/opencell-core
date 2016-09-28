package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.dwh.Chart;


/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Chart")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartDto extends BaseDto {

	
    private static final long serialVersionUID = 2573963792647472501L;

    @XmlAttribute
    private String code;

    @XmlAttribute
    private String description;

    private MeasurableQuantityDto measurableQuantity;
    private String width = "500px";
    private String height = "300px";
    private String style;
    private String styleClass;
    private String extender;
    private Boolean visible = false;

    public ChartDto() {
        super();
    }

    public ChartDto(Chart chart) {
        super();
        setCode(chart.getCode());
        setDescription(chart.getDescription());
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

    public MeasurableQuantityDto getMeasurableQuantity() {
        return measurableQuantity;
    }

    public void setMeasurableQuantity(MeasurableQuantityDto measurableQuantity) {
        this.measurableQuantity = measurableQuantity;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getExtender() {
        return extender;
    }

    public void setExtender(String extender) {
        this.extender = extender;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("ChartDto [code=%s, description=%s, measurableQuantityDto=%s, width=%s, height=%s, style=%s, styleClass=%s, extender=%s, visible=%s]", code,
            description, measurableQuantity, width, height, style, styleClass, extender, visible);
    }
}