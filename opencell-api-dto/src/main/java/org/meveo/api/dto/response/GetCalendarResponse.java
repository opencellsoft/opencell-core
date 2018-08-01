package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CalendarDto;

/**
 * The Class GetCalendarResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetCalendarResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCalendarResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2550428385118895687L;

    /** The calendar. */
    private CalendarDto calendar;

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public CalendarDto getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(CalendarDto calendar) {
        this.calendar = calendar;
    }

    @Override
    public String toString() {
        return "GetCalendarResponse [calendar=" + calendar + ", toString()=" + super.toString() + "]";
    }

}
