package org.meveo.api.dto.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.communication.Campaign;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.ContactGroup;

public class ContactGroupDto extends BaseEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3825293576254711373L;

	private String name;
	
	private	String description;
	
	private String type;
    
    private List<ContactDto> contact;
    
    private List<CampaignDto> campaign;

//    private AddressBookDto addressBook;

    
	public ContactGroupDto() {
		// TODO Auto-generated constructor stub
	}
	
	public ContactGroupDto(ContactGroup contactGroups) {
		name = contactGroups.getName();
		description = contactGroups.getDescription();
		type = contactGroups.getType();
		
//		addressBook = new AddressBookDto(contactGroups.getAddressBook());
		
		Set<Contact> contacts = contactGroups.getContacts();
		
		if(contacts != null) {
			List<ContactDto> contactDtos= new ArrayList<ContactDto>();
			for(Contact c : contacts) {
				ContactDto cd = new ContactDto(c);
				contactDtos.add(cd);
			}
			contact = contactDtos;
		}
		
		Set<Campaign> campaigns = contactGroups.getCampaigns();
		
		if(campaigns != null) {
			List<CampaignDto> campaignDtos = new ArrayList<CampaignDto>();
			for(Campaign c : campaigns) {
				CampaignDto cd = new CampaignDto(c);
				campaignDtos.add(cd);
			}
			campaign = campaignDtos;
		}
	}

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

	public List<ContactDto> getContact() {
		return contact;
	}

	public void setContact(List<ContactDto> contact) {
		this.contact = contact;
	}


	public List<CampaignDto> getCampaign() {
		return campaign;
	}

	public void setCampaign(List<CampaignDto> campaign) {
		this.campaign = campaign;
	}

//	public AddressBookDto getAddressBook() {
//		return addressBook;
//	}
//
//	public void setAddressBook(AddressBookDto addressBook) {
//		this.addressBook = addressBook;
//	}

	
}
