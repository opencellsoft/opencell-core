package org.meveo.api.dto.response.communication;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name="EmailTemplateResponse")
public class EmailTemplateResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6442797527168418565L;
	private EmailTemplateDto emailTemplate;

	public EmailTemplateDto getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplateDto emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
}

