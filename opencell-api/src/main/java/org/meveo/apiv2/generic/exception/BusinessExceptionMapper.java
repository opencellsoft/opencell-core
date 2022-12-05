package org.meveo.apiv2.generic.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.admin.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(Response.Status.BAD_REQUEST);

    @Override
    public Response toResponse(BusinessException exception) {
        log.error("A business exception occurred ", exception);
        return Response.status(Response.Status.BAD_REQUEST).entity(exceptionSerializer.toApiError(exception)).type(MediaType.APPLICATION_JSON)
                .header(Validation.VALIDATION_HEADER, "true").build();
    }
}
