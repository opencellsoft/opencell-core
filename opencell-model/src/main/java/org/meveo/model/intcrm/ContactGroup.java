package org.meveo.model.intcrm;

import java.util.HashSet;
import java.util.Set;

import org.meveo.model.security.Role;

public class ContactGroup {
	private String name;
	private	String description;
	private String type;
    private Set<ContactGroup> contactGroups = new HashSet<ContactGroup>();
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Set<ContactGroup> getContactGroups() {
		return contactGroups;
	}
	public void setContactGroups(Set<ContactGroup> contactGroups) {
		this.contactGroups = contactGroups;
	}

	
}
