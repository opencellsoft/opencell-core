package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BusinessAccountModelsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessAccountModelsResponseDto extends BaseResponse {

	private static final long serialVersionUID = 4582093808713253788L;

	private List<BusinessAccountModelDto> businessAccountModels;

	public List<BusinessAccountModelDto> getBusinessAccountModels() {
		return businessAccountModels;
	}

	public void setBusinessAccountModels(List<BusinessAccountModelDto> businessAccountModels) {
		this.businessAccountModels = businessAccountModels;
	}

}
