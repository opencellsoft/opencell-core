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
import org.meveo.model.catalog.CalendarDateInterval;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarJoin;
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
	private String code;

	private String description;

	@XmlElement(required = true)
	private String calendarType;

	private List<DayInYearDto> days;

	private List<HourInDayDto> hours;

	private Integer periodLength;

	private Integer periodUnit;

	private Integer nbPeriods;

	private String joinCalendar1Code;
	
	private String joinCalendar2Code;

    private String joinType;

    private String intervalType;    

    private List<CalendarDateIntervalDto> intervals;
	
	public CalendarDto() {
	}

	public CalendarDto(Calendar e) {
		code = e.getCode();
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
			periodUnit = calendar.getPeriodUnit();
			nbPeriods = calendar.getNbPeriods();
		
		} else if (e instanceof CalendarInterval) {
		    CalendarInterval calendar = (CalendarInterval) e;
		    intervalType = calendar.getIntervalType().name();
		    
		    if (calendar.getIntervals() != null && calendar.getIntervals().size() > 0) {
                intervals = new ArrayList<CalendarDateIntervalDto>();
                for (CalendarDateInterval d : calendar.getIntervals()) {
                    intervals.add(new CalendarDateIntervalDto(d));
                }
            }
            
		} else if (e instanceof CalendarJoin) {
		    CalendarJoin calendar = (CalendarJoin) e;
            
		    joinCalendar1Code = calendar.getJoinCalendar1().getCode();
		    joinCalendar2Code = calendar.getJoinCalendar2().getCode();
		    joinType =calendar.getJoinType().name();
        }
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
        final int maxLen = 10;
        return "CalendarDto [code=" + code + ", description=" + description + ", calendarType=" + calendarType + ", days="
                + (days != null ? days.subList(0, Math.min(days.size(), maxLen)) : null) + ", hours=" + (hours != null ? hours.subList(0, Math.min(hours.size(), maxLen)) : null)
                + ", periodLength=" + periodLength + ", periodUnit=" + periodUnit + ", nbPeriods=" + nbPeriods + ", joinType=" + joinType + ", joinCalendar1Code="
                + joinCalendar1Code + ", joinCalendar2Code=" + joinCalendar2Code + ", intervalType=" + intervalType + ", intervals="
                + (intervals != null ? intervals.subList(0, Math.min(intervals.size(), maxLen)) : null) + "]";
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

	public Integer getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(Integer periodUnit) {
		this.periodUnit = periodUnit;
	}

	public Integer getNbPeriods() {
		return nbPeriods;
	}

	public void setNbPeriods(Integer nbPeriods) {
		this.nbPeriods = nbPeriods;
	}

    public String getJoinCalendar1Code() {
        return joinCalendar1Code;
    }

    public void setJoinCalendar1Code(String joinCalendar1Code) {
        this.joinCalendar1Code = joinCalendar1Code;
    }

    public String getJoinCalendar2Code() {
        return joinCalendar2Code;
    }

    public void setJoinCalendar2Code(String joinCalendar2Code) {
        this.joinCalendar2Code = joinCalendar2Code;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public List<CalendarDateIntervalDto> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<CalendarDateIntervalDto> intervals) {
        this.intervals = intervals;
    }
}