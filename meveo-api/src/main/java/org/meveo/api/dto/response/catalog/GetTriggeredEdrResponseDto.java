package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetTriggeredEdrResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTriggeredEdrResponseDto extends BaseResponse {

	private static final long serialVersionUID = -408801271188966214L;

	private TriggeredEdrTemplateDto triggeredEdrTemplate;

	public TriggeredEdrTemplateDto getTriggeredEdrTemplate() {
		return triggeredEdrTemplate;
	}

	public void setTriggeredEdrTemplate(TriggeredEdrTemplateDto triggeredEdrTemplate) {
		this.triggeredEdrTemplate = triggeredEdrTemplate;
	}

	@Override
	public String toString() {
		return "GetTriggeredEdrResponseDto [triggeredEdrTemplate=" + triggeredEdrTemplate + ", getActionStatus()=" + getActionStatus() + "]";
	}

}
