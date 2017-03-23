package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.TimerEntityDto;

/**
 * @author Tyshan Shi
 *
 **/
@XmlRootElement(name = "TimerEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTimerEntityResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TimerEntityDto timerEntity;

	public TimerEntityDto getTimerEntity() {
		return timerEntity;
	}

	public void setTimerEntity(TimerEntityDto timerEntity) {
		this.timerEntity = timerEntity;
	}

	@Override
	public String toString() {
		return "TimerEntityResponseDto [timerEntity=" + timerEntity + "]";
	}

}
