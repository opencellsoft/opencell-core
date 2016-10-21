/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "COM_SENDER_CONFIG", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@DiscriminatorColumn(name = "MEDIA")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_SNDR_CONF_SEQ")
public abstract class MessageSenderConfig extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEDIA", insertable = false, updatable = false)
	private MediaEnum media;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRIORITY")
	private PriorityEnum defaultPriority;

	@Type(type="numeric_boolean")
    @Column(name = "MANAGE_NON_DISTRIB")
	private Boolean manageNonDistributedMessage;

	@Column(name = "NON_DISTRIB_EMAIL", length = 255)
    @Size(max = 255)
	private String NonDistributedEmail;

	@Type(type="numeric_boolean")
    @Column(name = "USE_ACK")
	private Boolean useAcknoledgement;

	@Column(name = "ACK_EMAIL", length = 255)
    @Size(max = 255)
	private String ackEmail;

	@Embedded
	private CommunicationPolicy senderPolicy;

	public MediaEnum getMedia() {
		return media;
	}

	public void setMedia(MediaEnum media) {
		this.media = media;
	}

	public PriorityEnum getDefaultPriority() {
		return defaultPriority;
	}

	public void setDefaultPriority(PriorityEnum defaultPriority) {
		this.defaultPriority = defaultPriority;
	}

	public Boolean isManageNonDistributedMessage() {
		return manageNonDistributedMessage;
	}

	public void setManageNonDistributedMessage(Boolean manageNonDistributedMessage) {
		this.manageNonDistributedMessage = manageNonDistributedMessage;
	}

	public String getNonDistributedEmail() {
		return NonDistributedEmail;
	}

	public void setNonDistributedEmail(String nonDistributedEmail) {
		NonDistributedEmail = nonDistributedEmail;
	}

	public Boolean isUseAcknoledgement() {
		return useAcknoledgement;
	}

	public void setUseAcknoledgement(Boolean useAcknoledgement) {
		this.useAcknoledgement = useAcknoledgement;
	}

	public String getAckEmail() {
		return ackEmail;
	}

	public void setAckEmail(String ackEmail) {
		this.ackEmail = ackEmail;
	}

	public CommunicationPolicy getSenderPolicy() {
		return senderPolicy;
	}

	public void setSenderPolicy(CommunicationPolicy senderPolicy) {
		this.senderPolicy = senderPolicy;
	}

}
