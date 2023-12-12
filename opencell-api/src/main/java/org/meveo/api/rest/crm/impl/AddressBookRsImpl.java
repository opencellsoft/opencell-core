/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.crm.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.crm.AddressBookApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.crm.GetAddressBookResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.crm.AddressBookRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.ContactService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Interceptors({ WsRestApiInterceptor.class })
public class AddressBookRsImpl extends BaseRs implements AddressBookRs {

	@Inject
	AddressBookApi addressBookApi;
	
	@Inject
	ContactService contactService;
	
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

	@Override
	public ActionStatus addContact(String addrCode, String ctCode) {
		ActionStatus result = new ActionStatus();
		
		try {
			addressBookApi.addContact(addrCode, ctCode);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}
	

}
