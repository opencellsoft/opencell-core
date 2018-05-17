package org.meveo.service.intcrm.impl;


import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.service.base.PersistenceService;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;

@Stateless
public class ContactService extends PersistenceService<Contact> {
	
	
    public void create(Contact contact) throws BusinessException {
        super.create(contact);
    }
    
    public Contact parse(String line){
    	log.debug("Parsing Contact Line : " + line);
		Contact c = new Contact();
		
		String[] split = line.split(",");
		
		String firstName = split[0];
		String lastName = split[1];
		String address = split[2];
		String email = split[3];
		String company = split[4];
		String connectedOn = split[5];
		String website = split[6];
		String instantMessengers = split[7];
		
		c.setName(new Name(new Title("M", false), firstName, lastName));
		c.setEmail(email);
		
		c.setContactCode(split[0]+split[1]);
			
		
		return c;		
	}
	
    public void saveContact(String line){
    	log.debug("Saving Contact Service");
    	Contact c = parse(line);
    	try {
			this.create(c);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
