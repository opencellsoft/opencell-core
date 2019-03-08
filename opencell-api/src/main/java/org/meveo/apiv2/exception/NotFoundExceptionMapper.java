package org.meveo.apiv2.exception;

import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getCause())
                .type(MediaType.TEXT_PLAIN).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
