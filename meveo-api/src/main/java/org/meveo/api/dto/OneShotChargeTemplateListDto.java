package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OneShotChargeTemplateList")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeTemplateListDto {
	
	private List<OneShotChargeTemplateDto> oneShotChargeTemplateDtos = new ArrayList<OneShotChargeTemplateDto>();

	public List<OneShotChargeTemplateDto> getOneShotChargeTemplateDtos() {
		return oneShotChargeTemplateDtos;
	}

	public void setOneShotChargeTemplateDtos(
			List<OneShotChargeTemplateDto> oneShotChargeTemplateDtos) {
		this.oneShotChargeTemplateDtos = oneShotChargeTemplateDtos;
	}
}
