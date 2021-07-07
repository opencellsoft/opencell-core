package org.meveo.model.report.query;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

@Embeddable
public class QueryTimer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2335885769252517249L;
	
	@Column(name = "year")
    private String year;

	@Column(name = "month")
    private String month;
	
	@Type(type = "numeric_boolean")
	@NotNull
	@Column(name = "every_month", nullable = false)
    private boolean everyMonth;
	
	@Column(name = "day_month")
    private String dayOfMonth;
	
	@Type(type = "numeric_boolean")
	@NotNull
	@Column(name = "every_day_month", nullable = false)
    private boolean everyDayOfMonth;
	
	@Column(name = "day_week")
    private String dayOfWeek;
	
	@Type(type = "numeric_boolean")
	@NotNull
	@Column(name = "every_day_week", nullable = false)
    private boolean everyDayOfWeek;
	
	@Column(name = "hour")
    private String hour;
	
	@Type(type = "numeric_boolean")
	@NotNull
	@Column(name = "every_hour", nullable = false)
    private boolean everyHour;
	
	@Column(name = "minute")
    private String minute;
	
	@Type(type = "numeric_boolean")
	@NotNull
	@Column(name = "every_minute", nullable = false)
    private boolean everyMinute;
	
	@Column(name = "second")
    private String second;
	
	@Type(type = "numeric_boolean")
	@NotNull
	@Column(name = "every_second", nullable = false)
    private boolean everySecond;
	
	

	public QueryTimer() {
		super();
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public boolean isEveryMonth() {
		return everyMonth;
	}

	public void setEveryMonth(boolean everyMonth) {
		this.everyMonth = everyMonth;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public boolean isEveryDayOfMonth() {
		return everyDayOfMonth;
	}

	public void setEveryDayOfMonth(boolean everyDayOfMonth) {
		this.everyDayOfMonth = everyDayOfMonth;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public boolean isEveryDayOfWeek() {
		return everyDayOfWeek;
	}

	public void setEveryDayOfWeek(boolean everyDayOfWeek) {
		this.everyDayOfWeek = everyDayOfWeek;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public boolean isEveryHour() {
		return everyHour;
	}

	public void setEveryHour(boolean everyHour) {
		this.everyHour = everyHour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public boolean isEveryMinute() {
		return everyMinute;
	}

	public void setEveryMinute(boolean everyMinute) {
		this.everyMinute = everyMinute;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public boolean isEverySecond() {
		return everySecond;
	}

	public void setEverySecond(boolean everySecond) {
		this.everySecond = everySecond;
	}

}