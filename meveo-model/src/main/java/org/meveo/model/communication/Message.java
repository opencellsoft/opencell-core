/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.communication.contact.Contact;

@Entity
@Table(name = "COM_MESSAGE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_MESSAGE_SEQ")
public class Message extends BaseEntity {

	private static final long serialVersionUID = 2760596592135889373L;

	@JoinColumn(name = "TEMPLATE_CODE")
	private String templateCode;

	@OneToMany(mappedBy = "message")
	private List<MessageVariableValue> parameters;

	@ManyToOne
	@JoinColumn(name = "CAMPAIGN_ID")
	private Campaign campaign;

	@ManyToOne
	@JoinColumn(name = "CONTACT_ID")
	private Contact contact;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEDIA")
	MediaEnum media;

	@Column(name = "SUB_MEDIA")
	String subMedia;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRIORITY")
	PriorityEnum priority;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	MessageStatusEnum status;

	@Column(name = "REJECTION_REASON")
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
