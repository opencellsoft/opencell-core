package org.meveo.api.dto.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.BusinessEntityDto;

import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;

public class AddressBookDto extends BusinessEntityDto  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1144224800558842204L;

	private List<ContactDto> contact;
	
	public AddressBookDto() {

	}
	

	public AddressBookDto(AddressBook addressBook) {
		super(addressBook);
				
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


	public List<ContactDto> getContact() {
		return contact;
	}


	public void setContact(List<ContactDto> contact) {
		this.contact = contact;
	}	
}
