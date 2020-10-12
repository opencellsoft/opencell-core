package org.meveo.apiv2.generic.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(Response.Status.NOT_FOUND);

    @Override
    public Response toResponse(NotFoundException exception) {
        log.error("A not found exception occurred ", exception);
        return Response.status(Response.Status.NOT_FOUND).entity(exceptionSerializer.toApiError(exception))
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
