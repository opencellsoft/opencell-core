package org.meveo.api.dto.response;

import org.meveo.api.dto.DataCollectorDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
