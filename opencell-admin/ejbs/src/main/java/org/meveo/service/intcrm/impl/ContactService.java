package org.meveo.service.intcrm.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CSVUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Country;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.TitleService;
import org.slf4j.Logger;

@Stateless
public class ContactService extends PersistenceService<Contact> {

	@Inject
	private TitleService titleService;

	@Inject
	private Logger log;
	
	public void create(Contact contact) throws BusinessException {
		super.create(contact);
	}
	
	public Set<Contact> parseLinkedInFromText(String context) {
		log.debug(context);
		Set<Contact> contacts = new HashSet<Contact>();
		BufferedReader br = null;
		
		br = new BufferedReader(new StringReader(context));
		
		String available;
        try {
        	boolean headerLine = true;
			while((available = br.readLine()) != null) {
				if(headerLine)headerLine = false;
				else {
					List<String> line = CSVUtils.parseLine(available);
					Contact c = new Contact();
					
					String firstName = line.get(0);
					String lastName = line.get(1);
					String strAddress = line.get(2);
					String email = line.get(3);
					String code = email;
					String company = line.get(4);
					String position = line.get(5);
					//Date connectedOn = new Date(line.get(6));
					String websiteUrl = line.get(7);
					String instantMessengers = line.get(8);
					String importedBy = this.currentUser.getFullName();
					
					Title title = titleService.findByCode("Mr.");
					c.setName(new Name(title, firstName, lastName));
					c.setEmail(email);
					c.setCode(code);
					c.setPosition(position);
					c.setWebsiteUrl(websiteUrl);
					c.setSocialIdentifier(instantMessengers);
					c.setImportedBy(importedBy);
					Address address = new Address(strAddress,"","","", "", null , "");
					c.setAddress(address);
					c.setAgreedToUA(false);
					c.setMessages(null);
					
					contacts.add(c);
				    System.out.println(line.get(0) + " " + line.get(1) + " " + line.get(2));
				}
				for(Contact c: contacts) {
					this.create(c);
				}
			}
		} catch (IOException | BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return contacts;
	}
	
	public Set<Contact> parseLinkedInFile(File file) {
		String csvPath;
		Set<Contact> contacts = null;
		if(file==null) {
			csvPath = System.getProperty("jboss.server.temp.dir") + "\\Connections.csv";
			file = new File(csvPath);
		}
		
		log.debug(file.getPath());
		
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
			contacts = parseLinkedInFromText(new String(encoded,  Charset.defaultCharset()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contacts;
	}
	
	public Contact parse(String line) {
		log.debug("Parsing Contact Line : " + line);
		Contact c = new Contact();

		String[] split = line.split(",");

		String firstName = split[0];
		String lastName = split[1];
		String strAddress = split[2];
		String email = split[3];
		String company = split[4];
		String connectedOn = split[5];
		String website = split[6];
		String instantMessengers = split[7];

		Title title = titleService.findByCode("Mr.");
		c.setName(new Name(title, firstName, lastName));
		c.setEmail(email);
		Address address = new Address(strAddress,"","","", "", null , "");
		c.setAddress(address);
		c.setAgreedToUA(false);

		c.setCode(split[0] + split[1]);

		return c;
	}

	public void saveContact(String line) {
		log.debug("Saving Contact Service");
		Contact c = parse(line);
		try {
			this.create(c);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			log.error("Save Contact Failed : " + e.toString());
		}
	}
	
	public void findContactByCode(Long id) {
		Contact contact = this.findById(id);
		log.debug("Long id = " + id);
		log.debug(contact.toString());
	}
	
	@SuppressWarnings("unchecked")
    public List<Contact> getAllContacts() {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }
	
	public void removeAllContacts() throws BusinessException {
		List<Contact> contacts = getAllContacts();
		for(Contact e : contacts) {
			log.debug("Removing : " + e.getName().toString());
			super.remove(e);
			
		}
	}

	
}
