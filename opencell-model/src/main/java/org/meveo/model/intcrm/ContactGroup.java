package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.Campaign;
import org.meveo.model.communication.contact.Contact;


@Entity
@ExportIdentifier({ "code" })
@Table(name = "crm_contact_group")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_contact_group_seq") })
public class ContactGroup extends BaseEntity{

	
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;
	
	@Column(name = "description", length = 255)
	@Size(max = 255)	
	private	String description;
	
	@Column(name = "type", length = 50)
	@Size(max = 50)
	private String type;
    
    @ManyToMany(mappedBy = "contactGroups", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Contact> contacts = new ArrayList<>();
    
    @ManyToMany(mappedBy = "contactGroups", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Campaign> campaigns;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adress_book_id")
    private AddressBook adressBook;

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

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

    /**
     * @return the adressBook
     */
    public AddressBook getAdressBook() {
        return adressBook;
    }

    /**
     * @param adressBook the adressBook to set
     */
    public void setAdressBook(AddressBook adressBook) {
        this.adressBook = adressBook;
    }
}
