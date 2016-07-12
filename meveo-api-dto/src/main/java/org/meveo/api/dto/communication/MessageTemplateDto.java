package org.meveo.api.dto.communication;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageTemplate;
import org.meveo.model.communication.MessageTemplateTypeEnum;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jul 10, 2016 9:18:59 PM
 **/
@XmlRootElement(name="MessageTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageTemplateDto extends BaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2370984261457651138L;
	@XmlAttribute(required=true)
	private String code;
	@XmlAttribute
	private String description;
	private MediaEnum media=MediaEnum.EMAIL;
	private String tagStartDelimiter="#{";
	private String tagEndDelimiter="}";
	private Date startDate;
	private Date endDate;
	private MessageTemplateTypeEnum type;
	
	public MessageTemplateDto(){}
	public MessageTemplateDto(MessageTemplate messageTemplate){
		this.code=messageTemplate.getCode();
		this.description=messageTemplate.getDescription();
		this.media=messageTemplate.getMedia();
		this.tagStartDelimiter=messageTemplate.getTagStartDelimiter();
		this.tagEndDelimiter=messageTemplate.getTagEndDelimiter();
		this.startDate=messageTemplate.getStartDate();
		this.endDate=messageTemplate.getEndDate();
		this.type=messageTemplate.getType();
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
	
	
}

