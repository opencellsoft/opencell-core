package org.meveo.admin.action.crm;

import javax.inject.Inject;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.intcrm.impl.ContactService;

public class ContactListBean extends BaseBean<Contact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9212281464363229994L;
	
	
	@Inject
	private ContactService contactService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ContactListBean() {
		super(Contact.class);
	}


	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Contact> getPersistenceService() {
		return contactService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
}
