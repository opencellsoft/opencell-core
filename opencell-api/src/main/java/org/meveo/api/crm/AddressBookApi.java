package org.meveo.api.crm;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.service.intcrm.impl.AddressBookService;


@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class AddressBookApi  extends BaseApi {
	@Inject
	AddressBookService addressBookService;
	
	public void createAll() throws BusinessException {
		addressBookService.createAll();
	}
	
	public void addContact(String code, Contact contact) {
		
	}
}
