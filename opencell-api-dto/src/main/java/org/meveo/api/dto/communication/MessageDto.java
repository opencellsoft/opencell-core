package org.meveo.api.dto.communication;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.communication.Campaign;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageStatusEnum;
import org.meveo.model.communication.MessageVariableValue;
import org.meveo.model.communication.PriorityEnum;
import org.meveo.model.communication.contact.Contact;

public class MessageDto extends BaseEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4362333981638941531L;

	private String templateCode;

	private List<MessageVariableValue> parameters;

	private Campaign campaign;

	private Contact contact;

	private MediaEnum media;

	private String subMedia;

	private PriorityEnum priority;

	private MessageStatusEnum status;

	private String rejectionReason;

	public MessageDto() {
	
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public List<MessageVariableValue> getParameters() {
		return parameters;
	}

	public void setParameters(List<MessageVariableValue> parameters) {
		this.parameters = parameters;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public MediaEnum getMedia() {
		return media;
	}

	public void setMedia(MediaEnum media) {
		this.media = media;
	}

	public String getSubMedia() {
		return subMedia;
	}

	public void setSubMedia(String subMedia) {
		this.subMedia = subMedia;
	}

	public PriorityEnum getPriority() {
		return priority;
	}

	public void setPriority(PriorityEnum priority) {
		this.priority = priority;
	}

	public MessageStatusEnum getStatus() {
		return status;
	}

	public void setStatus(MessageStatusEnum status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

}
