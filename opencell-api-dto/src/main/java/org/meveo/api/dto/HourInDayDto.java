package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.HourInDay;

/**
 * The Class HourInDayDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "HourInDay")
@XmlAccessorType(XmlAccessType.FIELD)
public class HourInDayDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;

    /** The hour. */
    @XmlAttribute(required = true)
    private Integer hour;

    /** The min. */
    @XmlAttribute(required = true)
    private Integer min;

    /**
     * Instantiates a new hour in day dto.
     */
    public HourInDayDto() {

    }

    /**
     * Instantiates a new hour in day dto.
     *
     * @param d the d
     */
    public HourInDayDto(HourInDay d) {
        hour = d.getHour();
        min = d.getMinute();
    }

    /**
     * Gets the hour.
     *
     * @return the hour
     */
    public Integer getHour() {
        return hour;
    }

    /**
     * Sets the hour.
     *
     * @param hour the new hour
     */
    public void setHour(Integer hour) {
        this.hour = hour;
    }

    /**
     * Gets the min.
     *
     * @return the min
     */
    public Integer getMin() {
        return min;
    }

    /**
     * Sets the min.
     *
     * @param min the new min
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HourInDayDto [hour=" + hour + ", min=" + min + "]";
    }
}