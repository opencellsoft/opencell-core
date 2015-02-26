package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarPeriod;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.HourInDay;

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

	@XmlElement(required = true)
	private String calendarType;

	private List<DayInYearDto> days;

	private List<HourInDayDto> hours;

	private Integer periodLength;

	private String periodUnit;

	private Integer nbPeriods;

	public CalendarDto() {
	}

	public CalendarDto(Calendar e) {
		name = e.getName();
		description = e.getDescription();
		calendarType = e.getCalendarType();

		if (e instanceof CalendarYearly) {
			CalendarYearly calendar = (CalendarYearly) e;
			if (calendar.getDays() != null && calendar.getDays().size() > 0) {
				days = new ArrayList<DayInYearDto>();
				for (DayInYear d : calendar.getDays()) {
					days.add(new DayInYearDto(d));
				}
			}
		} else if (e instanceof CalendarDaily) {
			CalendarDaily calendar = (CalendarDaily) e;
			if (calendar.getHours() != null && calendar.getHours().size() > 0) {
				hours = new ArrayList<HourInDayDto>();
				for (HourInDay d : calendar.getHours()) {
					hours.add(new HourInDayDto(d));
				}
			}

		} else if (e instanceof CalendarPeriod) {
			CalendarPeriod calendar = (CalendarPeriod) e;
			periodLength = calendar.getPeriodLength();
			periodUnit = "day";
			nbPeriods = calendar.getNbPeriods();
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

	public String getCalendarType() {
		return calendarType;
	}

	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	@Override
	public String toString() {
		return "CalendarDto [name=" + name + ", description=" + description + ", calendarType=" + calendarType
				+ ", days=" + days + ", hours=" + hours + ", periodLength=" + periodLength + ", periodUnit="
				+ periodUnit + ", nbPeriods=" + nbPeriods + "]";
	}

	public List<DayInYearDto> getDays() {
		return days;
	}

	public void setDays(List<DayInYearDto> days) {
		this.days = days;
	}

	public List<HourInDayDto> getHours() {
		return hours;
	}

	public void setHours(List<HourInDayDto> hours) {
		this.hours = hours;
	}

	public Integer getPeriodLength() {
		return periodLength;
	}

	public void setPeriodLength(Integer periodLength) {
		this.periodLength = periodLength;
	}

	public String getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}

	public Integer getNbPeriods() {
		return nbPeriods;
	}

	public void setNbPeriods(Integer nbPeriods) {
		this.nbPeriods = nbPeriods;
	}
}