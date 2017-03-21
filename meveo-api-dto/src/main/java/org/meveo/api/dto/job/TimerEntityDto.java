package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.TimerEntity;

@XmlRootElement(name = "TimerEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityDto extends BusinessDto {

    private static final long serialVersionUID = 5166093858617578774L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

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

    public TimerEntityDto() {
    }

    public TimerEntityDto(TimerEntity timerEntity) {
        this.code = timerEntity.getCode();
        this.description = timerEntity.getDescription();
        this.year = timerEntity.getYear();
        this.month = timerEntity.getMonth();
        this.dayOfMonth = timerEntity.getDayOfMonth();
        this.dayOfWeek = timerEntity.getDayOfWeek();
        this.hour = timerEntity.getHour();
        this.minute = timerEntity.getMinute();
        this.second = timerEntity.getSecond();
    }

    public static TimerEntity fromDTO(TimerEntityDto dto, TimerEntity timerEntityToUpdate) {
        TimerEntity timerEntity = new TimerEntity();
        if (timerEntityToUpdate != null) {
            timerEntity = timerEntityToUpdate;
        }

        timerEntity.setCode(StringUtils.isBlank(dto.getUpdatedCode()) ? dto.getCode() : dto.getUpdatedCode());
        timerEntity.setDescription(dto.getDescription());
        timerEntity.setYear(dto.getYear());
        timerEntity.setMonth(dto.getMonth());
        timerEntity.setDayOfMonth(dto.getDayOfMonth());
        timerEntity.setDayOfWeek(dto.getDayOfWeek());
        timerEntity.setHour(dto.getHour());
        timerEntity.setMinute(dto.getMinute());
        timerEntity.setSecond(dto.getSecond());

        return timerEntity;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "TimerEntityDto [code=" + code + ", description=" + description + ", hour=" + hour + ", minute=" + minute + ", second=" + second + ", year=" + year + ", month="
                + month + ", dayOfMonth=" + dayOfMonth + ", dayOfWeek=" + dayOfWeek + "]";
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof TimerEntityDto)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        TimerEntityDto other = (TimerEntityDto) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }
        return true;
    }
}
