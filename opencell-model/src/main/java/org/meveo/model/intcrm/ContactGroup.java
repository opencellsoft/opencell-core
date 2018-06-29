package org.meveo.model.intcrm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
public class ContactGroup extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1688451557730177945L;

	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;
	
	@Column(name = "description", length = 255)
	@Size(max = 255)	
	private	String description;
	
	@Column(name = "type", length = 50)
	@Size(max = 50)
	private String type;
    
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "crm_contact_group_com_contact", joinColumns = @JoinColumn(name = "contact_group_id"), inverseJoinColumns = @JoinColumn(name = "contact_id"))
    private Set<Contact> contacts = new HashSet<Contact>();
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "com_campaign_crm_contact_group", joinColumns = @JoinColumn(name = "contact_group_id"), inverseJoinColumns = @JoinColumn(name = "campaign_id"))
    private Set<Campaign> campaigns;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id")
    private AddressBook addressBook;

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

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

    public Set<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Set<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	/**
     * @return the addressBook
     */
    public AddressBook getAddressBook() {
        return addressBook;
    }

    /**
     * @param addressBook the addressBook to set
     */
    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }
}
