package org.meveo.service.intcrm.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.service.base.PersistenceService;
import org.meveo.model.intcrm.AddressBook;

public class AddressBookService extends PersistenceService {

    public void create(AddressBook addressBook) throws BusinessException {
        super.create(addressBook);
    }
}
