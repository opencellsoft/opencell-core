package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.jobs.TimerEntity;

@XmlRootElement(name = "TimerEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityDto extends EnableBusinessDto {

    private static final long serialVersionUID = 5166093858617578774L;

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
        super(timerEntity);
        
        this.year = timerEntity.getYear();
        this.month = timerEntity.getMonth();
        this.dayOfMonth = timerEntity.getDayOfMonth();
        this.dayOfWeek = timerEntity.getDayOfWeek();
        this.hour = timerEntity.getHour();
        this.minute = timerEntity.getMinute();
        this.second = timerEntity.getSecond();
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
        return "TimerEntityDto [code=" + getCode() + ", description=" + getDescription() + ", hour=" + hour + ", minute=" + minute + ", second=" + second + ", year=" + year + ", month="
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
