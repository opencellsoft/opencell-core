/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.intcrm.impl;


import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;

/**
 * Provider contact service implementation.
 */
@Stateless
public class ImportContactService extends PersistenceService {
	@Inject
    private ContactService contactService;
	
	@Inject
    private AddressBookService addressBookService;


    public Contact parse(String line){
    	log.debug("Parsing Contact Line");
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
		
		c.setName(new Name(new Title(), firstName, lastName));
		c.setEmail(email);
			
		
		return c;		
	}
	
    public void saveContact(String line){
    	log.debug("Saving Contact Service");
    	Contact c = parse(line);
    	try {
    		log.debug("Creating Contact");
			contactService.create(c);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /*
     	saveContact(String line) {
		//parse your line here
		Contact c = parse(line);
		contactService.save(c);
		}
     */
}
