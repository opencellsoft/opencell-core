package org.meveo.apiv2.exception;

import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getCause())
                .type(MediaType.TEXT_PLAIN).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
