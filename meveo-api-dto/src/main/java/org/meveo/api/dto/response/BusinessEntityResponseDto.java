package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;

@XmlRootElement(name = "BusinessEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessEntityResponseDto extends BaseResponse {

	private static final long serialVersionUID = -7750620521980139640L;
	
	public List<BusinessEntityDto> businessEntities;

	public List<BusinessEntityDto> getBusinessEntities() {
		return businessEntities;
	}

	public void setBusinessEntities(List<BusinessEntityDto> businessEntities) {
		this.businessEntities = businessEntities;
	}

}
