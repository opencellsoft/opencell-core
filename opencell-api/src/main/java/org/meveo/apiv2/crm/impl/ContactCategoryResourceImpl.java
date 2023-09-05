package org.meveo.apiv2.crm.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.crm.ContactCategoryDto;
import org.meveo.apiv2.crm.ContactCategoryResource;
import org.meveo.apiv2.crm.service.ContactCategoryApiService;
import org.meveo.model.communication.contact.ContactCategory;

@Interceptors({ WsRestApiInterceptor.class })
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

	public Response update(String contactCategoryCode, ContactCategoryDto postData) {
		ContactCategory result = contactCategoryApiService.update(contactCategoryCode, postData);

        ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		responseStatus.setEntityId(result.getId());

		return Response.ok(responseStatus).build();
	}

	public Response delete(String contactCategoryCode) {
		ActionStatus responseStatus = new ActionStatus();
		try {
			contactCategoryApiService.delete(contactCategoryCode);
			responseStatus.setStatus(ActionStatusEnum.SUCCESS);
			return Response.ok(responseStatus).build();
		} catch (Exception e) {
			if(getSpecificCauseFromStack(e, ConstraintViolationException.class) != null) {
				throw new DeleteReferencedEntityException(ContactCategory.class, contactCategoryCode);
			}
			throw new BusinessException(e);
		}
	}
	
    public Throwable getSpecificCauseFromStack(Throwable e, Class<?> clazz) {
        while (e != null) {
            if (e.getClass().equals(clazz)) {
                return e;
            }
            e = e.getCause();
        }
        return null;
    }
	
}
