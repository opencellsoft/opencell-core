package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CalendarsDto;

/**
 * The Class ListCalendarResponse.
 *
 * @author Antonio A. Alejandro
 */
@XmlRootElement(name = "ListCalendarResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListCalendarResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8366882097461743155L;
    
    /** The calendars. */
    private CalendarsDto calendars = new CalendarsDto();

    /**
     * Sets the calendars.
     *
     * @param calendars the new calendars
     */
    public void setCalendars(CalendarsDto calendars) {
        this.calendars = calendars;
    }

    /**
     * Gets the calendars.
     *
     * @return the calendars
     */
    public CalendarsDto getCalendars() {
        return calendars;
    }

    @Override
    public String toString() {
        return "ListCalendarsResponse [calendars=" + calendars + ", toString()=" + super.toString() + "]";
    }
}