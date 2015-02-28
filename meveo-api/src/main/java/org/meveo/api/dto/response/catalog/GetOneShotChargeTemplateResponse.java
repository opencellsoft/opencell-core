package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetOneShotChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOneShotChargeTemplateResponse extends BaseResponse {

	private static final long serialVersionUID = 5607296225072318694L;
	
	private OneShotChargeTemplateDto oneShotChargeTemplate;

	public OneShotChargeTemplateDto getOneShotChargeTemplate() {
		return oneShotChargeTemplate;
	}

	public void setOneShotChargeTemplate(
			OneShotChargeTemplateDto oneShotChargeTemplate) {
		this.oneShotChargeTemplate = oneShotChargeTemplate;
	}

	@Override
	public String toString() {
		return "GetOneShotChargeTemplateResponse [oneShotChargeTemplate=" + oneShotChargeTemplate + ", toString()="
				+ super.toString() + "]";
	}

}
