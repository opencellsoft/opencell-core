package org.meveo.service.intcrm.impl;


import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.service.base.PersistenceService;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.security.MeveoUser;

@Stateless
public class AddressBookService extends PersistenceService<AddressBook> {

    public void create(AddressBook addressBook) throws BusinessException {
        super.create(addressBook);
    }
    
    public String getCurrentUser() {
    	return this.currentUser.getFullName();
    }
    
}
