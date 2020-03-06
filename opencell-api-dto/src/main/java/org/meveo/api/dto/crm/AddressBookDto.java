/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
