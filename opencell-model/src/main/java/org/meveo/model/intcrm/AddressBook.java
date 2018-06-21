package org.meveo.model.intcrm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
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

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "crm_addressBook_crm_contact_group", joinColumns = @JoinColumn(name = "crm_address_book_id"), inverseJoinColumns = @JoinColumn(name = "crm_contact_group_id"))
	private Set<ContactGroup> contactGroups = new HashSet<ContactGroup>();

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "crm_addressbook_com_contact", joinColumns = @JoinColumn(name = "crm_address_book_id"), inverseJoinColumns = @JoinColumn(name = "com_contact_id"))
	private Set<Contact> contacts = new HashSet<Contact>();

	public AddressBook() {

	}

	public AddressBook(String code) {
		this.setCode(code);
		
	}

	public Set<ContactGroup> getContactGroups() {
		return contactGroups;
	}

	public void setContactGroups(Set<ContactGroup> contactGroups) {
		this.contactGroups = contactGroups;
	}

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}
	
}
