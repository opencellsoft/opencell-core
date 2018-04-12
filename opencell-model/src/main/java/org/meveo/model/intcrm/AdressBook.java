package org.meveo.model.intcrm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.Message;
import org.meveo.model.communication.contact.Contact;

@Entity
@CustomFieldEntity(cftCodePrefix = "ADDBOOK")
@ExportIdentifier({ "code" })
//@DiscriminatorValue(value = "")
@Table(name = "crm_adressbook")
public class AdressBook {
	@Column(name = "name", length = 5)
	@Size(max = 50)
	private String name;
	
    @OneToMany(mappedBy = "crm_adressbook", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Contact> contacts = new HashSet<Contact>();

    @OneToMany(mappedBy = "crm_adressbook")
	private List<ContactGroup> groups;
}
