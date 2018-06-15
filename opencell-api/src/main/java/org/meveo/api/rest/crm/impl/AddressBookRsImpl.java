package org.meveo.api.rest.crm.impl;

import javax.inject.Inject;

import org.meveo.api.crm.AddressBookApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.rest.crm.AddressBookRs;
import org.meveo.api.rest.crm.ContactRs;
import org.meveo.api.rest.impl.BaseRs;

public class AddressBookRsImpl extends BaseRs implements AddressBookRs {

	@Inject
	AddressBookApi addressBookApi;
	
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

}
