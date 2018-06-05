package org.meveo.admin.action.intcrm;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.ContactService;
import org.slf4j.Logger;


@Named
@ViewScoped
public class ContactBean extends CustomFieldBean<Contact>{
	@Inject
	private ContactService contactService;
	
	@Inject	
	private Logger log;
	
	public ContactBean() {
		super(Contact.class);
	}
	
	public void importContacts() {
		log.debug("Importing Contacts");
		contactService.parseFile();
		//contactService.saveContact("Arnaud,UHLRICH,,arnaud.uhlrich@free.Fr,Magellan Partners - ASAPpro,Senior Manager - Director,\"10/20/13 9:34AM\",,SKYPE:uhlricha");
	}
	
	public void findContactByCode() {
		log.debug("Finding Contact");
		contactService.findContactByCode((long) 3);
		log.debug("Contact Found");
	}
	
	public void removeAllContacts() throws BusinessException {
		log.debug("Removing Contacts");
		contactService.removeAllContacts();
		log.debug("Contacts Removed");
	}
	@PostConstruct
	private void init() {
		log.debug("start ContactBean");
	}
	

	@Override
	protected IPersistenceService<Contact> getPersistenceService() {
		return contactService;
	}
}
