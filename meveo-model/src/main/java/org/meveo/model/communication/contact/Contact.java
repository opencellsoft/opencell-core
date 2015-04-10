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
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;

@Entity
@ExportIdentifier({ "contactCode", "provider" })
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
