package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.CalendarDateInterval;

/**
 * The Class CalendarDateIntervalDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CalendarDateInterval")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarDateIntervalDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;

    /** The interval begin. */
    @XmlAttribute(required = true)
    private Integer intervalBegin;

    /** The interval end. */
    @XmlAttribute(required = true)
    private Integer intervalEnd;

    /**
     * Instantiates a new calendar date interval dto.
     */
    public CalendarDateIntervalDto() {

    }

    /**
     * Instantiates a new calendar date interval dto.
     *
     * @param d the d
     */
    public CalendarDateIntervalDto(CalendarDateInterval d) {
        intervalBegin = d.getIntervalBegin();
        intervalEnd = d.getIntervalEnd();
    }

    /**
     * Gets the interval begin.
     *
     * @return the interval begin
     */
    public Integer getIntervalBegin() {
        return intervalBegin;
    }

    /**
     * Sets the interval begin.
     *
     * @param intervalBegin the new interval begin
     */
    public void setIntervalBegin(Integer intervalBegin) {
        this.intervalBegin = intervalBegin;
    }

    /**
     * Gets the interval end.
     *
     * @return the interval end
     */
    public Integer getIntervalEnd() {
        return intervalEnd;
    }

    /**
     * Sets the interval end.
     *
     * @param intervalEnd the new interval end
     */
    public void setIntervalEnd(Integer intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CalendarDateIntervalDto [intervalBegin=" + intervalBegin + ", intervalEnd=" + intervalEnd + "]";
    }
}