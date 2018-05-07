package org.meveo.service.intcrm.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.service.base.PersistenceService;
import org.meveo.model.communication.contact.Contact;


public class ContactService extends PersistenceService {
	
	
    public void create(Contact contact) throws BusinessException {
        super.create(contact);
    }
    
    public void save(Contact c){
    	System.out.println("Saving Contact...");
    }
}
