package org.meveo.apiv2.documentCategory.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.documentCategory.DocumentCategoryDto;
import org.meveo.apiv2.documentCategory.resource.DocumentCategoryResource;
import org.meveo.apiv2.documentCategory.service.DocumentCategoryApiService;
import org.meveo.model.document.DocumentCategory;

@Interceptors({ WsRestApiInterceptor.class })
public class DocumentCategoryResourceImpl implements DocumentCategoryResource{
	
	@Inject
	private DocumentCategoryApiService documentCategoryApiService;
	
	public Response create(DocumentCategoryDto postData) {
        DocumentCategory result = documentCategoryApiService.create(postData);

        ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		responseStatus.setEntityId(result.getId());

		return Response.ok(responseStatus).build();
	}

	public Response update(Long id, DocumentCategoryDto postData) {
		DocumentCategory result = documentCategoryApiService.update(id, postData);

        ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		responseStatus.setEntityId(result.getId());

		return Response.ok(responseStatus).build();
	}

	public Response delete(Long id) {
		ActionStatus responseStatus = new ActionStatus();
		try {
			documentCategoryApiService.delete(id);
			responseStatus.setStatus(ActionStatusEnum.SUCCESS);
			return Response.ok(responseStatus).build();
		} catch (Exception e) {
			if(getSpecificCauseFromStack(e, ConstraintViolationException.class) != null) {
				throw new DeleteReferencedEntityException(DocumentCategory.class, id);
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
