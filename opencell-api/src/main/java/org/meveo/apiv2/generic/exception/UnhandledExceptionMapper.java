package org.meveo.apiv2.generic.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(Exception exception) {
        log.error("An unhandled exception occurred ", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new InternalServerErrorException("A server exception occurred"))
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
