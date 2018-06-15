package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.Message;
import org.meveo.model.communication.contact.Contact;

@Entity
@ExportIdentifier({ "addressbook" })
@Table(name = "crm_addressbook")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "crm_addressbook_seq"), })
public class AddressBook extends BusinessEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6638793926019456947L;

	@OneToMany(mappedBy = "addressBook", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ContactGroup> groups = new ArrayList<ContactGroup>();

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "crm_addressbook_contact", joinColumns = @JoinColumn(name = "address_book_id"), inverseJoinColumns = @JoinColumn(name = "contact_id"))
	private Set<Contact> contacts = new HashSet<Contact>();

	public AddressBook() {

	}

	public AddressBook(String code) {
		this.setCode(code);
	}

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<ContactGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ContactGroup> groups) {
		this.groups = groups;
	}
}
