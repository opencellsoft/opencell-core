package org.meveo.api.rest.crm.impl;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import org.meveo.api.crm.ContactApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.rest.crm.ContactRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.communication.contact.Contact;
import org.meveo.service.intcrm.impl.ContactService;

public class ContactRsImpl extends BaseRs implements ContactRs {

	@Inject
	ContactApi contactApi;

	@Override
	public ActionStatus create(ContactDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            contactApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }
		return result;
	}

	@Override
	public ActionStatus update(ContactDto postData) {
        ActionStatus result = new ActionStatus();

        try {
        	contactApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }
		return result;
	}

	@Override
	public ActionStatus remove(@PathParam("code") String code) {
        ActionStatus result = new ActionStatus();

        try {
        	contactApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(ContactDto postData) {
        ActionStatus result = new ActionStatus();
        
		return result;
	}


	@Override
	public String find(String code) {
		Contact contact = contactApi.findByCode(code);
		return contact.toString();
	}
}
