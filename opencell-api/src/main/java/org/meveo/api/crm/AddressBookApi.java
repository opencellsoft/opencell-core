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

package org.meveo.api.crm;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

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
				user = userService.findByUsername(code, false);
				if(user != null)
					addressBook = null;//user.getAddressbook();
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
