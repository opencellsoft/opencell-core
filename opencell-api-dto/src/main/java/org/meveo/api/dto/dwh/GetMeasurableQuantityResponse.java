package org.meveo.api.dto.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetMeasurableQuantityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetMeasurableQuantityResponse extends BaseResponse {	
	private static final long serialVersionUID = 1L;
	
	private MeasurableQuantityDto measurableQuantityDto;
	
	
	public MeasurableQuantityDto getMeasurableQuantityDto() {
		return measurableQuantityDto;
	}
	public void setMeasurableQuantityDto(MeasurableQuantityDto measurableQuantityDto) {
		this.measurableQuantityDto = measurableQuantityDto;
	}
	
	@Override
	public String toString() {
		return "GetMeasurableQuantityResponse [measurableQuantityDto=" + (measurableQuantityDto == null ? null : measurableQuantityDto)  + "]";
	}

}
