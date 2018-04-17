package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class TimerEntityResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "TimerEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6709030583427915931L;

    /** The timer entity. */
    private TimerEntityDto timerEntity;

    /**
     * Gets the timer entity.
     *
     * @return the timer entity
     */
    public TimerEntityDto getTimerEntity() {
        return timerEntity;
    }

    /**
     * Sets the timer entity.
     *
     * @param timerEntity the new timer entity
     */
    public void setTimerEntity(TimerEntityDto timerEntity) {
        this.timerEntity = timerEntity;
    }
}