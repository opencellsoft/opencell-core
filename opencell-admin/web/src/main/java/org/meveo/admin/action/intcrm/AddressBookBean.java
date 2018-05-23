package org.meveo.admin.action.intcrm;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.slf4j.Logger;

@Named
@ViewScoped
public class AddressBookBean extends CustomFieldBean<AddressBook>{
	@Inject
	AddressBookService addressBookService;

	@Inject	
	private Logger log;
	
	public AddressBookBean() {
		super(AddressBook.class);
	}

	@PostConstruct
	public void init() {
		log.debug("Start AddressBookBean");
	}
	
	
	@Override
	protected IPersistenceService<AddressBook> getPersistenceService() {
		return addressBookService;
	}
	
	public void currentUserHasAddressBook() {
		log.debug("Current User : " + addressBookService.getCurrentUser());
	}
	
}
