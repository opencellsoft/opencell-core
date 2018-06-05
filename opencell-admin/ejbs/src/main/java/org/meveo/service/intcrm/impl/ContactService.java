package org.meveo.service.intcrm.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

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

	public void parseFile() {
		String csvFile = System.getProperty("jboss.server.temp.dir") + "\\Connections.csv";
		log.debug(csvFile);
		
		
        Scanner scanner;
		try {
			scanner = new Scanner(new File(csvFile));
			
			while (scanner.hasNext()) {
	            List<String> line = CSVUtils.parseLine(scanner.nextLine());
	            System.out.println(line.get(0) + " " + line.get(1) + " " + line.get(2));
	        }
	        scanner.close();
		} catch (FileNotFoundException e) {
			log.debug(e.toString());
		}
        
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
