package org.meveo.admin.action.intcrm;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.ImportContactService;
import org.slf4j.Logger;

@Named
@ViewScoped
public class ImportContactBean extends CustomFieldBean<Contact> {

	@Inject
	private ImportContactService importContactService;
	
	@Inject	
	private Logger log;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ImportContactBean() {
		super(Contact.class);
	}

	public void importContacts() {
		System.out.println("Importing Contact Bean");
		 log.debug("Importing Contact Bean");
//		 importContactService.saveContact("Arnaud,UHLRICH,,arnaud.uhlrich@free.Fr,Magellan
		// Partners - ASAPpro,Senior Manager - Director,\"10/20/13, 9:34
		// AM\",,SKYPE:uhlricha");
	}

	@PostConstruct
	private void init() {
		log.debug("start");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Contact> getPersistenceService() {
		return importContactService;
	}	

}
