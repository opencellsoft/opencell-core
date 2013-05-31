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
package org.meveo.model.communication.contact;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseEntity;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;

@Entity
@Table(name = "COM_CONTACT", uniqueConstraints = @UniqueConstraint(columnNames = { "PROVIDER_ID",
		"CONTACT_CODE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_CONTACT_SEQ")
public class Contact extends BaseEntity {

	private static final long serialVersionUID = 3772773449495155646L;

	// It is provider resposibility to create contacts with unique codes
	@Column(name = "CONTACT_CODE", length = 50)
	String contactCode;

	@Embedded
	CommunicationPolicy contactPolicy;

	@OneToMany
	List<Message> messages;

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public CommunicationPolicy getContactPolicy() {
		return contactPolicy;
	}

	public void setContactPolicy(CommunicationPolicy contactPolicy) {
		this.contactPolicy = contactPolicy;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

}
