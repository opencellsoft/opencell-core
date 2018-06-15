package org.meveo.service.intcrm.impl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.ContactGroup;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class AddressBookService extends BusinessService<AddressBook> {

	@Inject
	private UserService userService;
	
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
        
        for(User u : users) {
        	if(u.getAddressbook() == null) {
        		AddressBook addressBook = new AddressBook(u.getUserName());
        		u.setAddressbook(addressBook);
        		userService.update(u);
            	this.create(addressBook);
        	}
        }
        
        
        for(Customer c : customers) {
        	if(c.getAddressbook() == null) {
        		AddressBook addressBook = new AddressBook(c.getCode());
        		c.setAddressbook(addressBook);
        		customerService.update(c);
            	this.create(addressBook);
        	}
        }
        
        for(CustomerAccount ca : customerAccounts) {
        	if(ca.getAddressbook() == null) {
        		AddressBook addressBook = new AddressBook(ca.getCode());
        		ca.setAddressbook(addressBook);
        		customerAccountService.update(ca);
            	this.create(addressBook);
        	}        
        }
    }

    public AddressBook getCurrentUserAddressBook() throws BusinessException {
    	String code = userService.findByUsername(currentUser.getUserName()).getUserName();
    	AddressBook addressBook = findByCode(code);
    	if (addressBook == null) {
    		addressBook = new AddressBook(code);
        	this.create(addressBook);
    	}
    	return addressBook;
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
    	addressBook.getContacts().add(contact);
    	this.update(addressBook);
    }
    
    public void addContacts(String code, Set<Contact> contacts) throws BusinessException {
    	AddressBook addressBook;
    	if(code != null) {
	    	addressBook = findByCode(code);
	    	if(addressBook == null) throw new BusinessException("No addressBook found with code : " + code);
	    	
    	}else {
	   		 addressBook = getCurrentUserAddressBook();
	   	}
    	addressBook.getContacts().addAll(contacts);
	   	this.update(addressBook);
    }
}
