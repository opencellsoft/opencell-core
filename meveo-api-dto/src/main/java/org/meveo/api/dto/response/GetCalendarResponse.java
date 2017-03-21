package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CalendarDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetCalendarResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCalendarResponse extends BaseResponse {

	private static final long serialVersionUID = 2550428385118895687L;

	private CalendarDto calendar;

	public CalendarDto getCalendar() {
		return calendar;
	}

	public void setCalendar(CalendarDto calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString() {
		return "GetCalendarResponse [calendar=" + calendar + ", toString()=" + super.toString() + "]";
	}

}
