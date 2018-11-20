package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.CalendarHoliday;

/**
 * The Class CalendarHolidayDto.
 *
 * @author hznibar
 */
@XmlRootElement(name = "CalendarHoliday")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarHolidayDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;

    /** The holiday begin. */
    @XmlAttribute(required = true)
    private Integer holidayBegin;

    /** The holiday end. */
    @XmlAttribute(required = true)
    private Integer holidayEnd;

    /**
     * Instantiates a new calendar date holiday dto.
     */
    public CalendarHolidayDto() {

    }

    /**
     * Instantiates a new calendar holiday dto.
     *
     * @param holiday the holiday
     */
    public CalendarHolidayDto(CalendarHoliday holiday) {
        holidayBegin = holiday.getHolidayBegin();
        holidayEnd = holiday.getHolidayEnd();
    }

    /**
     * Gets the holiday begin.
     *
     * @return the holiday begin
     */
    public Integer getHolidayBegin() {
        return holidayBegin;
    }

    /**
     * Sets the holiday begin.
     *
     * @param holidayBegin the new holiday begin
     */
    public void setHolidayBegin(Integer holidayBegin) {
        this.holidayBegin = holidayBegin;
    }

    /**
     * Gets the holiday end.
     *
     * @return the holiday end
     */
    public Integer getHolidayEnd() {
        return holidayEnd;
    }

    /**
     * Sets the holiday end.
     *
     * @param holidayEnd the new holiday end
     */
    public void setHolidayEnd(Integer holidayEnd) {
        this.holidayEnd = holidayEnd;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CalendarHolidayDto [holidayBegin=" + holidayBegin + ", holidayEnd=" + holidayEnd + "]";
    }
}