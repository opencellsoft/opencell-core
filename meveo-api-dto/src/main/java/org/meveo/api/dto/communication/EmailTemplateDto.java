package org.meveo.api.dto.communication;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageTemplateTypeEnum;
import org.meveo.model.communication.email.EmailTemplate;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 4:49:13 AM
 *
 */
@XmlType(name = "EmailTemplate")
@XmlRootElement(name = "EmailTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailTemplateDto extends BaseDto{

	private static final long serialVersionUID = 7503268654503865318L;
	@XmlElement(required=true)
	private String code;
	private String description;
	private MediaEnum media;
	private String tagStartDelimiter="#{";
	private String tagEndDelimiter="}";
	private Date startDate;
	private Date endDate;
	private MessageTemplateTypeEnum type;
	
	private String subject;
	private String htmlContent;
	private String textContent;
	public EmailTemplateDto(){
		super();
	}
	public EmailTemplateDto(EmailTemplate emailTemplate) {
		this.code=emailTemplate.getCode();
		this.description=emailTemplate.getDescription();
		this.media=emailTemplate.getMedia();
		this.tagStartDelimiter=emailTemplate.getTagStartDelimiter();
		this.tagEndDelimiter=emailTemplate.getTagEndDelimiter();
		this.startDate=emailTemplate.getStartDate();
		this.endDate=emailTemplate.getEndDate();
		this.type=emailTemplate.getType();
		this.subject=emailTemplate.getSubject();
		this.htmlContent=emailTemplate.getHtmlContent();
		this.textContent=emailTemplate.getTextContent();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public MediaEnum getMedia() {
		return media;
	}
	public void setMedia(MediaEnum media) {
		this.media = media;
	}
	public String getTagStartDelimiter() {
		return tagStartDelimiter;
	}
	public void setTagStartDelimiter(String tagStartDelimiter) {
		this.tagStartDelimiter = tagStartDelimiter;
	}
	public String getTagEndDelimiter() {
		return tagEndDelimiter;
	}
	public void setTagEndDelimiter(String tagEndDelimiter) {
		this.tagEndDelimiter = tagEndDelimiter;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public MessageTemplateTypeEnum getType() {
		return type;
	}
	public void setType(MessageTemplateTypeEnum type) {
		this.type = type;
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
