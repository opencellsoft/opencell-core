package org.meveo.service.intcrm.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CSVUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.communication.Message;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.slf4j.Logger;

@Stateless
public class ContactService extends BusinessService<Contact> {

	@Inject
	private TitleService titleService;

	@Inject
	private AddressBookService addressBookService;

	@Inject
	private Logger log;
	
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
	

	public Set<Contact> parseLinkedInFromText(String context) throws IOException {
		log.debug(context);
		Set<Contact> contacts = new HashSet<Contact>();

		try (BufferedReader br = new BufferedReader(new StringReader(context))) {

			String available;
			boolean headerLine = true;
			Title title = titleService.findByCode("MR");
			while ((available = br.readLine()) != null) {
				if (headerLine)
					headerLine = false;
				else {
					List<String> line = CSVUtils.parseLine(available);
					Contact c = new Contact();

					String firstName = line.get(0);
					String lastName = line.get(1);
					String strAddress = line.get(2);
					String email = line.get(3);
					String code = line.get(3);
					String company = line.get(4);
					String position = line.get(5);
					// Date connectedOn = new Date(line.get(6));
					String websiteUrl = line.get(7);
					String instantMessengers = line.get(8);
					String importedBy = this.currentUser.getFullName();
					List<Message> messages = new ArrayList<Message>();
					Message message = new Message();
					messages.add(message);

					c.setName(new Name(title, firstName, lastName));
					c.setEmail(email);
					c.setCode(code);
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
		if(!StringUtils.isBlank(contact.getCompany()))
			customer = customerService.findByCompanyName(contact.getCompany());
		if(customer != null) {
			contact.setAddressBook(customer.getAddressbook());
		}
		else {
			customer = createCustomerFromContact(contact);
		}
		super.create(contact);
	}
	
	
	public Set<Contact> parseLinkedInFile(File file) {
		String csvPath;
		Set<Contact> contacts = null;
		if (file == null) {
			csvPath = System.getProperty("jboss.server.temp.dir") + "\\Connections.csv";
			file = new File(csvPath);
		}

		log.debug(file.getPath());

		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
			contacts = parseLinkedInFromText(new String(encoded, Charset.defaultCharset()));
		} catch (IOException e) {
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
		
		contact.setAddressBook(addressBook);
		
		return customer;
	}
	
	public void logContactError(List<String> contactErrors) throws IOException {
		String path1 = System.getProperty("jboss.server.temp.dir") + "\\ContactError.log";
		String path2 = System.getProperty("jboss.server.temp.dir") + "\\LastContactError.log";
		
		try {
			FileWriter fw1 = new FileWriter(path1, true);
		    BufferedWriter bw1 = new BufferedWriter(fw1);
		    PrintWriter pwout1 = new PrintWriter(bw1);
		    
		    PrintWriter pwout2 = new PrintWriter(path2);
	
			for(String contactError : contactErrors) {
				pwout1.println(new Date().toString() + " | " + contactError);
				pwout2.println(new Date().toString() + " | " + contactError);
				
			}
			pwout1.close();
			pwout2.close();
			
		} catch (IOException e) {	
		    
		}
	}
}
