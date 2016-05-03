package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BusinessAccountModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessAccountModelResponseDto extends BaseResponse {

	private static final long serialVersionUID = 2059945254478663407L;

	private BusinessAccountModelDto businessAccountModel;

	public BusinessAccountModelDto getBusinessAccountModel() {
		return businessAccountModel;
	}

	public void setBusinessAccountModel(BusinessAccountModelDto businessAccountModel) {
		this.businessAccountModel = businessAccountModel;
	}

}
