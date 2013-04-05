/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.communication;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.communication.contact.Contact;

@Entity
@Table(name="COM_MESSAGE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_MESSAGE_SEQ")
public class Message extends BaseEntity {

	private static final long serialVersionUID = 2760596592135889373L;

	@JoinColumn(name="TEMPLATE_CODE")
	private String templateCode;
	
	@OneToMany(mappedBy="message")
	private List<MessageVariableValue> parameters;
	
	@ManyToOne 
	@JoinColumn(name="CAMPAIGN_ID")
	private Campaign campaign;
	
	@ManyToOne
	@JoinColumn(name="CONTACT_ID")
	private Contact contact;

	@Enumerated(EnumType.STRING)
	@Column(name="MEDIA")
	MediaEnum media;
	
	@Column(name="SUB_MEDIA")
	String subMedia;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name="PRIORITY")
	PriorityEnum priority;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS")
	MessageStatusEnum status;
	
	@Column(name="REJECTION_REASON")
	String rejectionReason;
	
	
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
