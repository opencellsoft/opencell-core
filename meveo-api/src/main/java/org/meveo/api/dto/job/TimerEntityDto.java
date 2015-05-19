package org.meveo.api.dto.job;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.jobs.TimerEntity;

 
@XmlRootElement(name = "TimerEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityDto extends BaseDto {

	private static final long serialVersionUID = 5166093858617578774L;
 

	@XmlElement(required = true)
	private String jobCategory; 
	
	@XmlAttribute(required = true)
	private String jobName;
	
	@XmlAttribute(required = true)
	private String name;
	
	@XmlAttribute(required = false) 
	private String followingTimer;

	@XmlAttribute(required = true) 
	private String hour = "*";
	
	@XmlAttribute(required = true) 
	private String minute = "0";
	
	@XmlAttribute(required = true) 
	private String second = "0";
	
	@XmlAttribute(required = true) 
	private String year = "*";
	
	@XmlAttribute(required = true) 
	private String month = "*";

	@XmlAttribute(required = true) 
	private String dayOfMonth = "*";
	
	@XmlAttribute(required = true) 
	private String dayOfWeek = "*";
	
	@XmlElement(required = false)
	private String parametres;
	
	@XmlElement(required = true)
	private boolean active = false;
	
	@XmlElement(required = false)
	private CustomFieldsDto customFields = new CustomFieldsDto();
	
	
	public TimerEntityDto() {	
	}
	
	public TimerEntityDto(TimerEntity t) {
		name = t.getName();
		jobName=t.getJobName();
		year=t.getYear();
		month=t.getMonth();
		dayOfMonth=t.getDayOfMonth();
		dayOfWeek=t.getDayOfWeek();
		hour=t.getHour();
		minute=t.getMinute();
		second=t.getSecond(); 
		parametres=t.getTimerInfo().getParametres();
		active=t.getTimerInfo().isActive();
		
		if(t.getJobCategoryEnum()!=null){
			jobCategory=t.getJobCategoryEnum().name();
			}
	
		if (t.getCustomFields() != null) {
			for (Map.Entry<String, CustomFieldInstance> entry : t.getCustomFields().entrySet()) {
				CustomFieldDto cfDto = new CustomFieldDto();
				cfDto.setCode(entry.getValue().getCode());
				cfDto.setDateValue(entry.getValue().getDateValue());
				cfDto.setDescription(entry.getValue().getDescription());
				cfDto.setDoubleValue(entry.getValue().getDoubleValue());
				cfDto.setLongValue(entry.getValue().getLongValue());
				cfDto.setStringValue(entry.getValue().getStringValue());
				customFields.getCustomField().add(cfDto);
			}
		}
	}

	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getJobName() {
		return jobName;
	}



	public void setJobName(String jobName) {
		this.jobName = jobName;
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



	public String getDayOfMonth() {
		return dayOfMonth;
	}



	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}



	public String getDayOfWeek() {
		return dayOfWeek;
	}



	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}



	public String getHour() {
		return hour;
	}



	public void setHour(String hour) {
		this.hour = hour;
	}



	public String getMinute() {
		return minute;
	}



	public void setMinute(String minute) {
		this.minute = minute;
	}



	public String getSecond() {
		return second;
	}



	public void setSecond(String second) {
		this.second = second;
	}


	public String getJobCategory() {
		return jobCategory;
	}



	public void setJobCategory(String jobCategory) {
		this.jobCategory = jobCategory;
	}



	public CustomFieldsDto getCustomFields() {
		return customFields;
	}



	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public String getFollowingTimer() {
		return followingTimer;
	}

	public void setFollowingTimer(String followingTimer) {
		this.followingTimer = followingTimer;
	}

	public String getParametres() {
		return parametres;
	}

	public void setParametres(String parametres) {
		this.parametres = parametres;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "TimerEntityDto [name=" + name + ", jobName=" + jobName + ", year=" + year + ", month=" + month + ", dayOfMonth="
				+ dayOfMonth + ", dayOfWeek=" + dayOfWeek + ", hour=" + hour + ", minute=" + minute + ", second="
				+ second + ", jobCategory=" + jobCategory + ", active=" + active +",followingTimer=" + followingTimer +",parametre=" + parametres +",customFields=" + customFields + "]";
	}

}
