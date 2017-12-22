package org.meveo.api.dto.response.communication;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 *
 */
@XmlRootElement(name="EmailTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailTemplatesResponseDto extends BaseResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7682857831421991842L;

	@XmlElementWrapper(name="emailTemplates")
	@XmlElement(name="emailTemplate")
	private List<EmailTemplateDto> emailTemplates;

	public List<EmailTemplateDto> getEmailTemplates() {
		return emailTemplates;
	}

	public void setEmailTemplates(List<EmailTemplateDto> emailTemplates) {
		this.emailTemplates = emailTemplates;
	}

}

