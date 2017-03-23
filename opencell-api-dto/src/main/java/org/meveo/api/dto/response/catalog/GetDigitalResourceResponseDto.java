package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DigitalResourcesDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetDigitalResourceResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDigitalResourceResponseDto extends BaseResponse {

	private static final long serialVersionUID = -7994925961817572482L;

	private DigitalResourcesDto digitalResourcesDto;

	public DigitalResourcesDto getDigitalResourcesDto() {
		return digitalResourcesDto;
	}
	
	public void setDigitalResourcesDto(DigitalResourcesDto digitalResourcesDto) {
		this.digitalResourcesDto = digitalResourcesDto;
	}

	@Override
	public String toString() {
		return "GetDigitalResourceResponseDto [digitalResource=" + digitalResourcesDto + "]";
	}
}
