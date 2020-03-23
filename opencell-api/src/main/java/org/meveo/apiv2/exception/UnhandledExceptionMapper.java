package org.meveo.apiv2.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer("500");

    @Override
    public Response toResponse(Exception exception) {
        log.error("An unhandled exception occurred ", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exceptionSerializer.toApiError(exception))
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
