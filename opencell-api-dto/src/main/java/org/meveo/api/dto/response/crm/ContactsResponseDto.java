package org.meveo.api.dto.response.crm;

import org.meveo.api.dto.crm.ContactsDto;
import org.meveo.api.dto.response.SearchResponse;

public class ContactsResponseDto extends SearchResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3916812149080503322L;

	private ContactsDto contacts = new ContactsDto();


	public ContactsDto getContacts() {
		return contacts;
	}


	public void setContacts(ContactsDto contacts) {
		this.contacts = contacts;
	}


	public String toString() {
        return "ContactsResponseDto [contacts=" + contacts + ", toString()=" + super.toString() + "]";
	}
}
