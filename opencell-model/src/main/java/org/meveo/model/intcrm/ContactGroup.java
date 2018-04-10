package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import org.meveo.model.communication.contact.Contact;

public class ContactGroup {
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;
	
	@Column(name = "description", length = 512)
	@Size(max = 512)	
	private	String description;
	
	@Column(name = "type", length = 50)
	@Size(max = 512)
	private String type;
    
    @OneToMany(mappedBy = "crm_contactgroup", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Contact> contacts = new ArrayList<>();
    
}
