package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.TimerEntityDto;

/**
 * The Class GetTimerEntityResponseDto.
 *
 * @author Tyshan Shi
 */
@XmlRootElement(name = "TimerEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTimerEntityResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

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

    @Override
    public String toString() {
        return "TimerEntityResponseDto [timerEntity=" + timerEntity + "]";
    }
}