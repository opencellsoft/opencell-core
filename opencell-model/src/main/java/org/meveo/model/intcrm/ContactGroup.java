package org.meveo.model.intcrm;

import java.util.ArrayList;
import java.util.List;

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

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.Campaign;
import org.meveo.model.communication.contact.Contact;


@Entity
@ExportIdentifier({ "code" })
@Table(name = "crm_contact_group")
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
    
    @OneToMany(mappedBy = "crm_contact_group", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Contact> contacts = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bi_campaign_group", joinColumns = @JoinColumn(name = "crm_group_id"), inverseJoinColumns = @JoinColumn(name = "com_campaign_id"))
    private List<Campaign> campaigns;

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
}
