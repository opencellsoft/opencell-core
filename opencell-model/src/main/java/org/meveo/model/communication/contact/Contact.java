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
package org.meveo.model.communication.contact;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AccountEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.ContactGroup;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "com_contact")
@DiscriminatorValue(value = "ACCT_CTACT")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "com_contact_seq"), })
public class Contact extends AccountEntity {

	private static final long serialVersionUID = 3772773449495155646L;
	
	 public static final String ACCOUNT_TYPE = ((DiscriminatorValue) Contact.class.getAnnotation(DiscriminatorValue.class)).value();

	@Column(name = "email", length = 255)
	@Size(max = 255)
	private String email;

	@Column(name = "assistant_name", length = 50)
	@Size(max = 50)
	private String assistantName;

	@Column(name = "assistant_phone", length = 15)
	@Size(max = 15)
	private String assistantPhone;

	@Column(name = "position", length = 100)
	@Size(max = 100)
	private String position;
	
	@Column(name = "website_url", length = 255)
	@Size(max = 255)
	private String websiteUrl;

	@Column(name = "imported_from", length = 50)
	@Size(max = 50)
	private String importedFrom;

	@Column(name = "imported_by", length = 50)
	@Size(max = 50)
	private String importedBy;

	@Column(name = "social_identifier", length = 2000)
	@Size(max = 2000)
	private String socialIdentifier;

	@Type(type = "numeric_boolean")
	@Column(name = "is_vip", columnDefinition = "tinyint default false")
	private boolean isVip;

	@Type(type = "numeric_boolean")
	@Column(name = "is_suspect", columnDefinition = "tinyint default yes")
	private boolean isSuspect;

	@Type(type = "numeric_boolean")
	@Column(name = "agreed_ua", columnDefinition = "tinyint default false")
	private boolean agreedToUA;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "crm_contact_group_com_contact", joinColumns = @JoinColumn(name = "com_contact_id"), inverseJoinColumns = @JoinColumn(name = "crm_contact_group_id"))
	private List<ContactGroup> contactGroups = new ArrayList<>();

	@Embedded
	private CommunicationPolicy contactPolicy;

	@OneToMany(mappedBy = "contact", fetch = FetchType.LAZY)
	private List<Message> messages;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_book_id")
	private AddressBook addressBook;

	public Contact() {
		accountType = ACCOUNT_TYPE;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAssistantName() {
		return assistantName;
	}

	public void setAssistantName(String assistantName) {
		this.assistantName = assistantName;
	}

	public String getAssistantPhone() {
		return assistantPhone;
	}

	public void setAssistantPhone(String assistantPhone) {
		this.assistantPhone = assistantPhone;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}


	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getImportedFrom() {
		return importedFrom;
	}

	public void setImportedFrom(String importedFrom) {
		this.importedFrom = importedFrom;
	}

	public String getImportedBy() {
		return importedBy;
	}

	public void setImportedBy(String importedBy) {
		this.importedBy = importedBy;
	}

	public String getSocialIdentifier() {
		return socialIdentifier;
	}

	public void setSocialIdentifier(String socialIdentifier) {
		this.socialIdentifier = socialIdentifier;
	}

	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

	public boolean isSuspect() {
		return isSuspect;
	}

	public void setSuspect(boolean isSuspect) {
		this.isSuspect = isSuspect;
	}

	public boolean isAgreedToUA() {
		return agreedToUA;
	}

	public void setAgreedToUA(boolean agreedToUA) {
		this.agreedToUA = agreedToUA;
	}

	public List<ContactGroup> getContactGroups() {
		return contactGroups;
	}

	public void setContactGroups(List<ContactGroup> contactGroups) {
		this.contactGroups = contactGroups;
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

	public AddressBook getAddressBook() {
		return addressBook;
	}

	public void setAddressBook(AddressBook addressBook) {
		this.addressBook = addressBook;
	}
}