package org.meveo.api.dto.response;

import org.meveo.api.dto.DataCollectorDto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataCollectorResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataCollectorResponse extends BaseResponse {

    private DataCollectorDto dataCollectorDto;

    public DataCollectorDto getDataCollectorDto() {
        return dataCollectorDto;
    }

    public void setDataCollectorDto(DataCollectorDto dataCollectorDto) {
        this.dataCollectorDto = dataCollectorDto;
    }
}
