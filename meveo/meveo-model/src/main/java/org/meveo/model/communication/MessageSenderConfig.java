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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name="COM_SENDER_CONFIG")
@DiscriminatorColumn(name = "MEDIA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_SNDR_CONF_SEQ")
public abstract class MessageSenderConfig extends BusinessEntity {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Enumerated(EnumType.STRING)
	@Column(name="MEDIA",insertable=false,updatable=false)
	MediaEnum media;
	

	@Enumerated(EnumType.STRING)
	@Column(name="PRIORITY")
	private PriorityEnum defaultPriority;
	

	@Column(name="MANAGE_NON_DISTRIB")
	private Boolean manageNonDistributedMessage;
	

	@Column(name="NON_DISTRIB_EMAIL")
	private String NonDistributedEmail;
	

	@Column(name="USE_ACK")
	private Boolean useAcknoledgement;
	

	@Column(name="ACK_EMAIL")
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

	public void setManageNonDistributedMessage(
			Boolean manageNonDistributedMessage) {
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
