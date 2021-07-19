package org.meveo.apiv2.generic.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class EntityDoesNotExistsExceptionMapper implements ExceptionMapper<EntityDoesNotExistsException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(Response.Status.BAD_REQUEST);

    @Override
    public Response toResponse(EntityDoesNotExistsException exception) {
        log.error("A business exception occurred ", exception);
        return Response.status(Response.Status.NOT_FOUND).entity(exceptionSerializer.toApiError(exception)).type(MediaType.APPLICATION_JSON)
                .header(Validation.VALIDATION_HEADER, "true").build();
    }

}
