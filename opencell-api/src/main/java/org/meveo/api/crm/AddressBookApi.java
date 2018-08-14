package org.meveo.api.crm;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.crm.AddressBookDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactService;
import org.meveo.service.payments.impl.CustomerAccountService;


@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class AddressBookApi  extends BaseApi {
	@Inject
	AddressBookService addressBookService;
	
	@Inject
	ContactService contactService;
	
	@Inject
	CustomerService customerService;
	
	@Inject
	CustomerAccountService customerAccountService;
	
	@Inject
	UserService userService;
	
	public void createAll() throws BusinessException {
		addressBookService.createAll();
	}
	
	public void addContact(String addrCode, String ctCode) throws BusinessException {
		Contact contact = contactService.findByCode(ctCode);
		AddressBook addressBook = addressBookService.findByCode(addrCode);
		
		if(contact == null)
			throw new BusinessException("Contact " + ctCode + " not found");
		if (addressBook == null)
			throw new BusinessException("AddressBook " + addrCode + " not found");
			
			
		contact.setAddressBook(addressBook);
		contact = contactService.update(contact);
		
		addressBook.getContacts().add(contact);
		addressBookService.update(addressBook);
	}
	
	public AddressBookDto findAddressBook(String code, String from) throws Exception {
		AddressBook addressBook = null;
		
		Customer customer;
		
		CustomerAccount customerAccount;
		
		User user;
		
		switch(from) {
			case "customer":
			case "Customer":
			case "c":
			case "C":
				customer = customerService.findByCode(code);
				if(customer != null)
					addressBook = customer.getAddressbook();
				else throw new EntityDoesNotExistsException(Customer.class, code);
				break;
			case "customerAccount":
			case "CustomerAccount":
			case "customeraccount":
			case "ca":
			case "CA":
				customerAccount = customerAccountService.findByCode(code);
				if(customerAccount != null)
					addressBook = customerAccount.getAddressbook();
				else throw new EntityDoesNotExistsException(CustomerAccount.class, code);
				break;
			case "user":
			case "User":
			case "u":
			case "U":
				user = userService.findByUsername(code);
				if(user != null)
					addressBook = user.getAddressbook();
				else throw new EntityDoesNotExistsException("User with UserName : " + code + " does not exist.");
				break;
			case "company":
			case "Company":
				customer = customerService.findByCompanyName(code);
				if(customer != null)
					addressBook = customer.getAddressbook();
				else throw new EntityDoesNotExistsException("Customer with company name : " + code + " does not exist.");
				break;
			default:
				throw new InvalidParameterException("from", from);
		}
		return new AddressBookDto(addressBook);
	}
}
