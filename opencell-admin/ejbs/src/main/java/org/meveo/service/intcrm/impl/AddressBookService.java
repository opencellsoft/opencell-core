package org.meveo.service.intcrm.impl;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;

@Stateless
public class AddressBookService extends PersistenceService<AddressBook> {

	@Inject
	private UserService userService;
	
    public void create(AddressBook addressBook) throws BusinessException {
        super.create(addressBook);
    }
    
    public String getCurrentUser() {
    	return this.currentUser.getFullName();
    }
    

	
	public void findAddressBookByCode(Long id) {
		id = (long) 1;
		AddressBook addressBook = this.findById(id);
		log.debug("Long id = " + id);
		log.debug(addressBook.toString());
	}
    
    public void createAddressBook() {
    	AddressBook addressBook = new AddressBook();
    	try {
			this.create(addressBook);
		} catch (BusinessException e) {
			log.error("Save AddressBook Failed : " + e.toString());
		}
    }
    
    public void addContactsToAddressBook(AddressBook addressBook, List<Contact> contacts) {
    	Set<Contact> addrContacts = addressBook.getContacts();
    	Set<Contact> newSet = new HashSet<Contact>();
    	for(Contact c : contacts) {
    		newSet.add(c);
    	}
    	addrContacts.addAll(newSet);
    	
    	log.debug("Contacts List has been added to AddressBook " + addressBook.getId());
    }
    

}
