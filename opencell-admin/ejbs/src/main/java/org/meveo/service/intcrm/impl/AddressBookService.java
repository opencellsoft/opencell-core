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

package org.meveo.service.intcrm.impl;


import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class AddressBookService extends BusinessService<AddressBook> {

	@Inject
	private UserService userService;
	
	@Inject
	private ContactService contactService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private CustomerAccountService customerAccountService;
	
    public void create(AddressBook addressBook) throws BusinessException {
        super.create(addressBook);
    }
    

    
    public void createAll() throws BusinessException {   	
        List<Customer> customers = customerService.list();
        List<CustomerAccount> customerAccounts = customerAccountService.list();
        List<User> users = userService.list();
        
        
        for(Customer c : customers) {
        	if(c.getAddressbook() == null) {
        		AddressBook addressBook = new AddressBook(c.getCode());
            	this.create(addressBook);
        		c.setAddressbook(addressBook);
        		customerService.update(c);
        	}
        }
        
        for(CustomerAccount ca : customerAccounts) {
        	if(ca.getAddressbook() == null) {
        		AddressBook addressBook = new AddressBook(ca.getCode());
            	this.create(addressBook);
        		ca.setAddressbook(addressBook);
        		customerAccountService.update(ca);
        	}        
        }
        

        for(User u : users) {
        	if(u.getAddressbook() == null) {
        		AddressBook addressBook = new AddressBook(u.getUserName());
            	this.create(addressBook);
        		u.setAddressbook(addressBook);
        		userService.update(u);
        	}
        }
        
    }

    public AddressBook getCurrentUserAddressBook() throws BusinessException {
    	User user = userService.findByUsername(currentUser.getUserName());
    	if(user.getAddressbook() == null) {
    		AddressBook addressBook = new AddressBook(user.getUserName());
        	this.create(addressBook);
    		user.setAddressbook(addressBook);
    		userService.update(user);
    		return addressBook;
    	}
    	else return user.getAddressbook();
    }
    
    public void addContact(String code, Contact contact) throws BusinessException {
    	AddressBook addressBook;
    	if(code != null) {
	    	addressBook = findByCode(code);
	    	if(addressBook == null) throw new BusinessException("No addressBook found with code : " + code);
	    	
    	}
    	else {
    		 addressBook = getCurrentUserAddressBook();
    	}
    	contact.setAddressBook(addressBook);
    	
		contactService.update(contact);
    }
    
    public void addContacts(String code, Set<Contact> contacts) throws BusinessException {
    	AddressBook addressBook;
    	if(code != null) {
	    	addressBook = findByCode(code);
	    	if(addressBook == null) throw new BusinessException("No addressBook found with code : " + code);
	    	
    	}else {
	   		 addressBook = getCurrentUserAddressBook();
	   	}
    	Set<Contact> cs = addressBook.getContacts();
    	cs.addAll(contacts);
	   	addressBook.setContacts(cs);
    	this.update(addressBook);
    }
}
