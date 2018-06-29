package org.meveo.api.dto.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.BusinessEntityDto;

import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.ContactGroup;

public class AddressBookDto extends BusinessEntityDto  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1144224800558842204L;

	private List<ContactGroupDto> contactGroup;
	
	private List<ContactDto> contact;
	
	public AddressBookDto() {

	}
	

	public AddressBookDto(AddressBook addressBook) {
		super(addressBook);
		
		Set<ContactGroup> contactGroups = addressBook.getContactGroups();
		
		if(contactGroups != null) {
			List<ContactGroupDto> contactGroupDtos= new ArrayList<ContactGroupDto>();
			for(ContactGroup cg : contactGroups) {
				ContactGroupDto cgd = new ContactGroupDto(cg);
				contactGroupDtos.add(cgd);
			}
			contactGroup = contactGroupDtos;
		}
		
		Set<Contact> contacts = addressBook.getContacts();
		
		if(contacts != null) {
			List<ContactDto> contactDtos= new ArrayList<ContactDto>();
			for(Contact c : contacts) {
				ContactDto cd = new ContactDto(c);
				contactDtos.add(cd);
			}
			contact = contactDtos;
		}
	}


	public List<ContactGroupDto> getContactGroup() {
		return contactGroup;
	}


	public void setContactGroup(List<ContactGroupDto> contactGroup) {
		this.contactGroup = contactGroup;
	}


	public List<ContactDto> getContact() {
		return contact;
	}


	public void setContact(List<ContactDto> contact) {
		this.contact = contact;
	}	
}
