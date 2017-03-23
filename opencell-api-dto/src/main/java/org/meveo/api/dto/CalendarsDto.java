package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Calendars")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarsDto implements Serializable {

	private static final long serialVersionUID = -6354285812403951307L;

	private List<CalendarDto> calendar;

	public List<CalendarDto> getCalendar() {
		if (calendar == null)
			calendar = new ArrayList<CalendarDto>();
		return calendar;
	}

	public void setCalendar(List<CalendarDto> calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString() {
		return "CalendarsDto [calendar=" + calendar + "]";
	}

}
