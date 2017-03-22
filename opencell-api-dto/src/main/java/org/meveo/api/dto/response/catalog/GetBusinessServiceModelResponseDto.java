package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetBusinessServiceModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBusinessServiceModelResponseDto extends BaseResponse {

	private static final long serialVersionUID = -6781250820569600144L;

	private BusinessServiceModelDto businessServiceModel;

	public BusinessServiceModelDto getBusinessServiceModel() {
		return businessServiceModel;
	}

	public void setBusinessServiceModel(BusinessServiceModelDto businessServiceModel) {
		this.businessServiceModel = businessServiceModel;
	}

	@Override
	public String toString() {
		return "GetBusinessServiceModelResponseDto [businessServiceModel=" + businessServiceModel + "]";
	}

}
