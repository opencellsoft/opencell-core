package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.HourInDay;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "HourInDay")
@XmlAccessorType(XmlAccessType.FIELD)
public class HourInDayDto implements Serializable {

    private static final long serialVersionUID = -980309137868444523L;

    @XmlAttribute(required = true)
    private Integer hour;

    @XmlAttribute(required = true)
    private Integer min;

    public HourInDayDto() {

    }

    public HourInDayDto(HourInDay d) {
        hour = d.getHour();
        min = d.getMinute();
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    @Override
	public String toString() {
		return "HourInDayDto [hour=" + hour + ", min=" + min + "]";
	}
}