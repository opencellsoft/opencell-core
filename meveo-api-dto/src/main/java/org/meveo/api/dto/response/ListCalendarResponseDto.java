package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CalendarsDto;

/**
 * @author Antonio A. Alejandro
 **/
@XmlRootElement(name = "ListCalendarResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListCalendarResponseDto extends BaseResponse{

	private static final long serialVersionUID = -8366882097461743155L;
	private CalendarsDto calendars = new CalendarsDto();

	public void setCalendars(CalendarsDto calendars) {
		this.calendars = calendars;
	}
	
	public CalendarsDto getCalendars() {
		return calendars;
	}

	@Override
	public String toString() {
		return "ListCalendarsResponseDto [calendars=" + calendars + ", toString()=" + super.toString() + "]";
	} 

}
