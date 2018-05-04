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


import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.job.Job;
import org.meveo.security.MeveoUser;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactService;;
import org.meveo.service.base.PersistenceService;
import org.meveo.model.shared.Title;
import org.meveo.model.shared.Name;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.model.billing.Subscription;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;

/**
 * Provider contact service implementation.
 */
@Stateless
public class ImportContactService extends PersistenceService {
	@Inject
    private ContactService contactService;
	
	@Inject
    private AddressBookService addressBookService;

		
	public void main() throws BusinessException {
		System.out.println("Main ImportingContactService");
		
		Contact contact = new Contact();
		//AddressBook addressBook = new AddressBook();
		
		Name name = new Name(new Title(), "Franck", "Valot");
		contact.setName(name);
		
		contactService.create(contact);
		//addressBookService.create(addressBook);
		
	}
}
