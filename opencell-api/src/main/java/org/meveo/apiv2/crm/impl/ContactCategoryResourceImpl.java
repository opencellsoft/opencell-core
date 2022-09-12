package org.meveo.apiv2.crm.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.crm.ContactCategoryDto;
import org.meveo.apiv2.crm.ContactCategoryResource;
import org.meveo.apiv2.crm.service.ContactCategoryApiService;
import org.meveo.model.communication.contact.ContactCategory;

public class ContactCategoryResourceImpl implements ContactCategoryResource {

	@Inject
	private ContactCategoryApiService contactCategoryApiService;
	
	public Response create(ContactCategoryDto postData) {
        ContactCategory result = contactCategoryApiService.create(postData);

        ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		responseStatus.setEntityId(result.getId());

		return Response.ok(responseStatus).build();
	}

	public Response update(ContactCategoryDto postData) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
