package org.meveo.apiv2.generic.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class EntityDoesNotExistsExceptionMapper implements ExceptionMapper<EntityDoesNotExistsException> {
    public static final Response.Status NOT_FOUND = Response.Status.NOT_FOUND;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(NOT_FOUND);

    @Override
    public Response toResponse(EntityDoesNotExistsException exception) {
        log.error("A business exception occurred ", exception);
        return Response.status(NOT_FOUND).entity(exceptionSerializer.toApiError(exception)).type(MediaType.APPLICATION_JSON)
                .header(Validation.VALIDATION_HEADER, "true").build();
    }

}
