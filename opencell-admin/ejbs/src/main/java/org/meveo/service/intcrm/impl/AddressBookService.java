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
