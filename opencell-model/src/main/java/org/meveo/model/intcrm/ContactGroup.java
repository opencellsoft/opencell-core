package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.contact.Contact;


@Entity
@ExportIdentifier({ "code" })
@Table(name = "crm_contact_group")
public class ContactGroup {
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;
	
	@Column(name = "description", length = 255)
	@Size(max = 255)	
	private	String description;
	
	@Column(name = "type", length = 50)
	@Size(max = 50)
	private String type;
    
    @OneToMany(mappedBy = "crm_contact_group", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Contact> contacts = new ArrayList<>();
    
}
