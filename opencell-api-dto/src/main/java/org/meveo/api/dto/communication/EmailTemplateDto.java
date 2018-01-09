package org.meveo.api.dto.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.communication.email.EmailTemplate;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 4:49:13 AM
 *
 */
@XmlRootElement(name = "EmailTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailTemplateDto extends MessageTemplateDto{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1739876218558380262L;
	@XmlElement(required=true)
	private String subject;
	private String htmlContent;
	private String textContent;
	public EmailTemplateDto(){
		super();
	}
	public EmailTemplateDto(EmailTemplate emailTemplate) {
		super(emailTemplate);
		this.subject=emailTemplate.getSubject();
		this.htmlContent=emailTemplate.getHtmlContent();
		this.textContent=emailTemplate.getTextContent();
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	
}
