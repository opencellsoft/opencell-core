package org.meveo.api.rest.crm.impl;

import javax.inject.Inject;

import org.meveo.api.crm.AddressBookApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.crm.AddressBookDto;
import org.meveo.api.dto.response.crm.GetAddressBookResponseDto;
import org.meveo.api.rest.crm.AddressBookRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

public class AddressBookRsImpl extends BaseRs implements AddressBookRs {

	@Inject
	AddressBookApi addressBookApi;
	
	@Inject
	CustomerService customerService;
	
	@Inject
	CustomerAccountService customerAccountService;
	
	@Inject
	UserService userService;
	
	@Override
	public ActionStatus createAll() {
		ActionStatus result = new ActionStatus();

		try {
			addressBookApi.createAll();
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(Long id, String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetAddressBookResponseDto find(String code, String from) {
		GetAddressBookResponseDto result = new GetAddressBookResponseDto();

		try {
			result.setAddressBook(addressBookApi.findAddressBook(code, from));	
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}
	

}
