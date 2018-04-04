package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@CustomFieldEntity(cftCodePrefix = "ADDBOOK")
@ExportIdentifier({ "code" })
//@DiscriminatorValue(value = "")
@Table(name = "crm_adressbook")
public class AdressBook {
	private String name;
    private Set<Contact> contacts = new HashSet<Contact>();
    
	private List<ContactGroup> groups;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
