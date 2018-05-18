package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.MonthEnum;

/**
 * The Class DayInYearDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "DayInYear")
@XmlAccessorType(XmlAccessType.FIELD)
public class DayInYearDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7771829024178269036L;

    /** The day. */
    @XmlAttribute(required = true)
    private Integer day;

    /** The month. */
    @XmlAttribute(required = true)
    private MonthEnum month;

    /**
     * Instantiates a new day in year dto.
     */
    public DayInYearDto() {

    }

    /**
     * Instantiates a new day in year dto.
     *
     * @param d the d
     */
    public DayInYearDto(DayInYear d) {
        day = d.getDay();

        if (d.getMonth() != null) {
            month = d.getMonth();
        }
    }

    /**
     * Gets the day.
     *
     * @return the day
     */
    public Integer getDay() {
        return day;
    }

    /**
     * Sets the day.
     *
     * @param day the new day
     */
    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public MonthEnum getMonth() {
        return month;
    }

    /**
     * Sets the month.
     *
     * @param month the new month
     */
    public void setMonth(MonthEnum month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "DayInYearDto [day=" + day + ", month=" + month + "]";
    }

}
