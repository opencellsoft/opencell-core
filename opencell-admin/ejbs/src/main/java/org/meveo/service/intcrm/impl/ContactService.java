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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.communication.Message;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;


@Stateless
public class ContactService extends BusinessService<Contact> {

	@Inject
	private TitleService titleService;

	@Inject
	private AddressBookService addressBookService;

	@Inject
	private CustomerService customerService;
	
	@Inject
	private SellerService sellerService;
	
	@Inject
	private CustomerBrandService customerBrandService;
	
	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private AdditionalDetailsService additionalDetailsService;
		
	public List<Contact> parseCSVText(String context) throws IOException, CsvException {
		log.debug(context);
		
		CSVReader reader = new CSVReader(new StringReader(context));
	    List<String[]> lines = reader.readAll();
		List<Contact> contacts = new ArrayList<Contact>();

		boolean headerLine = true;
		Title title = titleService.findByCode("MR");
		
		int firstNameIndex = -1;
		int lastNameIndex = -1;
		int strAddressIndex = -1;
		int emailIndex = -1;
		int codeIndex = -1;
		int companyIndex = -1;
		int positionIndex = -1;
		int websiteUrlIndex = -1;
		int instantMessengersIndex = -1;
		
		String firstName = null;
		String lastName = null;
		String strAddress = null;
		String email = null;
		String code = null;
		String company = null;
		String position = null;
		String websiteUrl = null;
		String instantMessengers = null;
		
		for (String[] line : lines) {
			if (headerLine) {
				headerLine = false;
				
				for(int i = 0; i < line.length; i++) {
					if(line[i].matches("First Name|Given Name")) firstNameIndex = i;
					else if(line[i].matches("Last Name|Family Name")) lastNameIndex = i;
					else if(line[i].matches("Address|Address 1 - Formatted|Business Address")) strAddressIndex = i;
					else if(line[i].matches("Email Address|E-mail 1 - Value|E-mail Address")) emailIndex = i;
					else if(line[i].matches("Company|Organization 1 - Name")) companyIndex = i;
					else if(line[i].matches("Position|Organization 1 - Title|Job Title")) positionIndex = i;
					else if(line[i].matches("Websites|Website 1 - Value|Web Page")) websiteUrlIndex = i;
					else if(line[i].matches("Instant Messengers|IM 1 - Value")) instantMessengersIndex = i;
				}
			}
			else {
				Contact c = new Contact();
				
				if(firstNameIndex > -1)
					firstName = line[firstNameIndex];
				if(lastNameIndex > -1)
					lastName = line[lastNameIndex];
				if(strAddressIndex > -1)
					strAddress = line[strAddressIndex];
				if(emailIndex > -1)
					email = line[emailIndex];
				if(companyIndex > -1)
					company = line[companyIndex];
				if(positionIndex > -1)
					position = line[positionIndex];
				//if(connectedOnINdex > -1)
					// Date connectedOn = new Date(line[6));
				if(websiteUrlIndex > -1)
					websiteUrl = line[websiteUrlIndex];
				if(instantMessengersIndex > -1)
					instantMessengers = line[instantMessengersIndex];
					
				String importedBy = this.currentUser.getFullName();
				List<Message> messages = new ArrayList<Message>();
				Message message = new Message();
				messages.add(message);

				c.setName(new Name(title, firstName, lastName));
                if (email != null) {
                    c.getContactInformationNullSafe().setEmail(email);
                }
				c.setCode(email);
				c.setPosition(position);
				c.setCompany(company);
				c.setWebsiteUrl(websiteUrl);
				c.setSocialIdentifier(instantMessengers);
				c.setImportedBy(importedBy);
				Address address = new Address(strAddress, "", "", "", "", null, "");
				c.setAddress(address);
				c.setAgreedToUA(false);
				c.setMessages(messages);

				contacts.add(c);
			}
		}
		

		return contacts;
	}

	public Set<Contact> create(Set<Contact> contacts) {
		Set<Contact> failedToPersistContacts = new HashSet<Contact>();

		for (Contact contact : contacts) {
			try {
				create(contact);
			} catch (BusinessException e) {
				log.debug("Failed to save contact : " + contact.toString());
				log.debug(e.getMessage());
				failedToPersistContacts.add(contact);
			}
		}

		return failedToPersistContacts;
	}

	public void create(Contact contact) throws BusinessException {
		
		Customer customer = null;
		if(contact.getCompany() == null || contact.getCompany().isEmpty()) {
			customer = customerService.findByCompanyName("UNASSIGNED");
			if(customer == null) customer = createUnassignedCustomer();
			contact.setAddressBook(customer.getAddressbook());
		}
		else {
			customer = customerService.findByCompanyName(contact.getCompany());
			if(customer != null) {
				contact.setAddressBook(customer.getAddressbook());
			}
			else {
				customer = createCustomerFromContact(contact);
			}
		}
		
		super.create(contact);
	}
	
	
	public List<Contact> parseCSVFile(File file) {
		String csvPath;
		List<Contact> contacts = null;
		if (file == null) {
			csvPath = System.getProperty("jboss.server.temp.dir") + "\\Connections.csv";
			file = new File(csvPath);
		}

		log.debug(file.getPath());

		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
			contacts = parseCSVText(new String(encoded, Charset.defaultCharset()));
		} catch (IOException | CsvException e) {
			log.error("Failed parsing file={}", e.getMessage());
		}

		return contacts;
	}

	public void removeAllContacts() throws BusinessException {
		List<Contact> contacts = list();
		for (Contact c : contacts) {
			log.debug("Removing : " + c.getName().toString());
			super.remove(c);

		}
	}
	
	public Customer createCustomerFromContact(Contact contact) throws BusinessException {
		Customer customer = new Customer();
		CustomerBrand customerBrand = customerBrandService.findByCode("DEFAULT");
		Seller seller = sellerService.findByCode("MAIN_SELLER");
		CustomerCategory customerCategory = customerCategoryService.findByCode("PROSPECT");
		
		customer.setCustomerBrand(customerBrand);
		customer.setSeller(seller);
		customer.setCustomerCategory(customerCategory);
		
		customer.setCode(contact.getCode());
		customer.setName(contact.getName());
		customer.setAddress(contact.getAddress());

        
        AdditionalDetails additionalDetails = new AdditionalDetails();
        additionalDetails.setCompanyName(contact.getCompany());
        additionalDetails.setPosition(contact.getPosition());
        additionalDetailsService.create(additionalDetails);
        
        AddressBook addressBook = new AddressBook("C_" + contact.getCode());
        addressBookService.create(addressBook);
        
        customer.setAdditionalDetails(additionalDetails);
        customer.setAddressbook(addressBook);	        
		customerService.create(customer);
		getEntityManager().flush();
		
		contact.setAddressBook(addressBook);
		
		return customer;
	}
	
	public Customer createUnassignedCustomer() throws BusinessException  {
		Customer customer = new Customer();
		CustomerBrand customerBrand = customerBrandService.findByCode("DEFAULT");
		Seller seller = sellerService.findByCode("MAIN_SELLER");
		CustomerCategory customerCategory = customerCategoryService.findByCode("PROSPECT");
		
		customer.setCustomerBrand(customerBrand);
		customer.setSeller(seller);
		customer.setCustomerCategory(customerCategory);
		
		customer.setCode("UNASSIGNED");

        
        AdditionalDetails additionalDetails = new AdditionalDetails();
        additionalDetails.setCompanyName("UNASSIGNED");
        additionalDetails.setPosition("UNASSIGNED");
        additionalDetailsService.create(additionalDetails);
        
        AddressBook addressBook = new AddressBook("C_UNASSIGNED");
        addressBookService.create(addressBook);
        
        customer.setAdditionalDetails(additionalDetails);
        customer.setAddressbook(addressBook);	        
		customerService.create(customer);
		getEntityManager().flush();
				
		return customer;
	}
	
	public void logContactError(List<String> contactErrors) throws IOException {
		String path1 = System.getProperty("jboss.server.temp.dir") + "\\ContactError.log";
		String path2 = System.getProperty("jboss.server.temp.dir") + "\\LastContactError.log";

		try (FileWriter fw1 = new FileWriter(path1, true)) {
			try (BufferedWriter bw1 = new BufferedWriter(fw1)) {
				try (PrintWriter pwout1 = new PrintWriter(bw1)) {
					try (PrintWriter pwout2 = new PrintWriter(path2)) {
						for (String contactError : contactErrors) {
							pwout1.println(new Date().toString() + " | " + contactError);
							pwout2.println(new Date().toString() + " | " + contactError);
						}
					}
				}
			}
		}
	}
	
	/**
     * Return all orders with orderDate date more than n years old
     * @param nYear age of the subscription
     * @return Filtered list of orders
     */
    @SuppressWarnings("unchecked")
	public List<Contact> listInactiveProspect(int nYear) {
    	QueryBuilder qb = new QueryBuilder(Contact.class, "e");
    	Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);
    	
    	qb.addCriterionDateRangeToTruncatedToDay("auditable.created", higherBound, true, false);
    	qb.addBooleanCriterion("isProspect", true);
    	
    	return (List<Contact>) qb.getQuery(getEntityManager()).getResultList();
    }

	public void bulkDelete(List<Contact> inactiveContacts) throws BusinessException {
		for (Contact e : inactiveContacts) {
			remove(e);
		}
	}
}
