package org.meveo.apiv2.generic.exception;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(Response.Status.FORBIDDEN);

    @Override
    public Response toResponse(ForbiddenException exception) {
        log.error("Forbidden exception occurred ", exception);
        return Response.status(Response.Status.FORBIDDEN).entity(exceptionSerializer.toApiError(exception)).type(MediaType.APPLICATION_JSON)
            .header(Validation.VALIDATION_HEADER, "true").build();
    }
}
