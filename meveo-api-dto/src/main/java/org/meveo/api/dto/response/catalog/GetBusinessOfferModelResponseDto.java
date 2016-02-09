package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetBusinessOfferModelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBusinessOfferModelResponseDto extends BaseResponse {

	private static final long serialVersionUID = -6781250820569600144L;

	private BusinessOfferModelDto businessOfferModel;

	public BusinessOfferModelDto getBusinessOfferModel() {
		return businessOfferModel;
	}

	public void setBusinessOfferModel(BusinessOfferModelDto businessOfferModel) {
		this.businessOfferModel = businessOfferModel;
	}

	@Override
	public String toString() {
		return "GetBusinessOfferModelResponseDto [businessOfferModel=" + businessOfferModel + "]";
	}

}
