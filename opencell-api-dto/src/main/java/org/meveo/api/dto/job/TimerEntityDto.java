package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.TimerEntity;

/**
 * The Class TimerEntityDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "TimerEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5166093858617578774L;

    /** The hour. */
    @XmlAttribute(required = true)
    private String hour = "*";

    /** The minute. */
    @XmlAttribute(required = true)
    private String minute = "0";

    /** The second. */
    @XmlAttribute(required = true)
    private String second = "0";

    /** The year. */
    @XmlAttribute(required = true)
    private String year = "*";

    /** The month. */
    @XmlAttribute(required = true)
    private String month = "*";

    /** The day of month. */
    @XmlAttribute(required = true)
    private String dayOfMonth = "*";

    /** The day of week. */
    @XmlAttribute(required = true)
    private String dayOfWeek = "*";

    /**
     * Instantiates a new timer entity dto.
     */
    public TimerEntityDto() {
    }

    /**
     * Instantiates a new timer entity dto.
     *
     * @param timerEntity the timer entity
     */
    public TimerEntityDto(TimerEntity timerEntity) {
        super(timerEntity);

        this.year = timerEntity.getYear();
        this.month = timerEntity.getMonth();
        this.dayOfMonth = timerEntity.getDayOfMonth();
        this.dayOfWeek = timerEntity.getDayOfWeek();
        this.hour = timerEntity.getHour();
        this.minute = timerEntity.getMinute();
        this.second = timerEntity.getSecond();
    }

    /**
     * From DTO.
     *
     * @param dto the dto
     * @param timerEntityToUpdate the timer entity to update
     * @return the timer entity
     */
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
     * Gets the year.
     *
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the new year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the month.
     *
     * @param month the new month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the day of month.
     *
     * @return the day of month
     */
    public String getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Sets the day of month.
     *
     * @param dayOfMonth the new day of month
     */
    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * Gets the day of week.
     *
     * @return the day of week
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the day of week.
     *
     * @param dayOfWeek the new day of week
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Gets the hour.
     *
     * @return the hour
     */
    public String getHour() {
        return hour;
    }

    /**
     * Sets the hour.
     *
     * @param hour the new hour
     */
    public void setHour(String hour) {
        this.hour = hour;
    }

    /**
     * Gets the minute.
     *
     * @return the minute
     */
    public String getMinute() {
        return minute;
    }

    /**
     * Sets the minute.
     *
     * @param minute the new minute
     */
    public void setMinute(String minute) {
        this.minute = minute;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public String getSecond() {
        return second;
    }

    /**
     * Sets the second.
     *
     * @param second the new second
     */
    public void setSecond(String second) {
        this.second = second;
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
    
    @Override
    public String toString() {
        return "TimerEntityDto [code=" + getCode() + ", description=" + getDescription() + ", hour=" + hour + ", minute=" + minute + ", second=" + second + ", year=" + year
                + ", month=" + month + ", dayOfMonth=" + dayOfMonth + ", dayOfWeek=" + dayOfWeek + "]";
    }
}
