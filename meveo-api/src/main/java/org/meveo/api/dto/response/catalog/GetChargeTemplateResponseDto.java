package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetChargeTemplateResponseDto extends BaseResponse {

	private static final long serialVersionUID = -7907466519449995575L;

	private ChargeTemplateDto chargeTemplate;

	public ChargeTemplateDto getChargeTemplate() {
		return chargeTemplate;
	}

	public void setChargeTemplate(ChargeTemplateDto chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}

	@Override
	public String toString() {
		return "GetChargeTemplateResponseDto [chargeTemplate=" + chargeTemplate + ", toString()=" + super.toString() + "]";
	}

}
