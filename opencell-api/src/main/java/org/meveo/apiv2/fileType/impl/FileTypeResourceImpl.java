package org.meveo.apiv2.fileType.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.crm.ContactCategoryResource;
import org.meveo.apiv2.fileType.FileTypeDto;
import org.meveo.apiv2.fileType.resource.FileTypeResource;
import org.meveo.apiv2.fileType.service.FileTypeApiService;
import org.meveo.model.admin.FileType;

@Interceptors({ WsRestApiInterceptor.class })
public class FileTypeResourceImpl implements FileTypeResource{
	
	@Inject
	private FileTypeApiService fileTypeApiService;
	
	public Response create(FileTypeDto postData) {
        FileType result = fileTypeApiService.create(postData);

        ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		responseStatus.setEntityId(result.getId());

		return Response.ok(responseStatus).build();
	}

	public Response update(Long id, FileTypeDto postData) {
		FileType result = fileTypeApiService.update(id, postData);

        ActionStatus responseStatus = new ActionStatus();
		responseStatus.setStatus(ActionStatusEnum.SUCCESS);
		responseStatus.setEntityId(result.getId());

		return Response.ok(responseStatus).build();
	}

	public Response delete(Long id) {
		ActionStatus responseStatus = new ActionStatus();
		try {
			fileTypeApiService.delete(id);
			responseStatus.setStatus(ActionStatusEnum.SUCCESS);
			return Response.ok(responseStatus).build();
		} catch (Exception e) {
			if(getSpecificCauseFromStack(e, ConstraintViolationException.class) != null) {
				throw new DeleteReferencedEntityException(FileType.class, id);
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
