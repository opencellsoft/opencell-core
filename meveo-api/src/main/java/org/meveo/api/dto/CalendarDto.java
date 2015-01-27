package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Calendar")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarDto extends BaseDto {

	private static final long serialVersionUID = 8269245242022483636L;

	@XmlAttribute(required = true)
	private String name;

	private String description;

	@XmlAttribute(required = true)
	private String type;

	private List<DayInYearDto> days;

	public CalendarDto() {
	}

	public CalendarDto(Calendar e) {
		name = e.getName();
		description = e.getDescription();

		if (e.getType() != null) {
			type = e.getType().name();
		}

		if (e instanceof CalendarYearly) {
			CalendarYearly calendarYearly = (CalendarYearly) e;
			if (calendarYearly.getDays() != null && calendarYearly.getDays().size() > 0) {
				days = new ArrayList<DayInYearDto>();
				for(DayInYear d : calendarYearly.getDays()) {
					days.add(new DayInYearDto(d));
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CalendarDto [name=" + name + ", description=" + description + ", type=" + type + ", dayInYear=" + days
				+ "]";
	}

	public List<DayInYearDto> getDays() {
		return days;
	}

	public void setDays(List<DayInYearDto> days) {
		this.days = days;
	}

}
