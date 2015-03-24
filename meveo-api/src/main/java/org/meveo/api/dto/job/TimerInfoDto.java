package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TimerInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerInfoDto extends BaseDto {

	private static final long serialVersionUID = -7091372162470026030L;

	@XmlElement(required = true)
	private String timerName;

	public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    @Override
	public String toString() {
		return "TimerInfoDto [timerName=" + timerName + ", toString()="
				+ super.toString() + "]";
	}

}
