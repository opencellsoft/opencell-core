package org.meveo.apiv2.generic.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(Response.Status.BAD_REQUEST);

    @Override
    public Response toResponse(ValidationException exception) {
        log.error("A bad request exception occurred ", exception);
        return Response.status(Response.Status.BAD_REQUEST).entity(exceptionSerializer.toApiError(exception))
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
