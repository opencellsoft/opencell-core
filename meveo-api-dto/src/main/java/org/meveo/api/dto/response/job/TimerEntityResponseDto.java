package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "TimerEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEntityResponseDto extends BaseResponse {

    private static final long serialVersionUID = -6709030583427915931L;

    private TimerEntityDto timerEntity;

    public TimerEntityDto getTimerEntity() {
        return timerEntity;
    }

    public void setTimerEntity(TimerEntityDto timerEntity) {
        this.timerEntity = timerEntity;
    }
}