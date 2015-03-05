package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.CalendarDateInterval;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "CalendarDateInterval")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarDateIntervalDto implements Serializable {

    private static final long serialVersionUID = -980309137868444523L;

    @XmlAttribute(required = true)
    private Integer intervalBegin;

    @XmlAttribute(required = true)
    private Integer intervalEnd;

    public CalendarDateIntervalDto() {

    }

    public CalendarDateIntervalDto(CalendarDateInterval d) {
        intervalBegin = d.getIntervalBegin();
        intervalEnd = d.getIntervalEnd();
    }

    public Integer getIntervalBegin() {
        return intervalBegin;
    }

    public void setIntervalBegin(Integer intervalBegin) {
        this.intervalBegin = intervalBegin;
    }

    public Integer getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(Integer intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    @Override
    public String toString() {
        return "CalendarDateIntervalDto [intervalBegin=" + intervalBegin + ", intervalEnd=" + intervalEnd + "]";
    }
}