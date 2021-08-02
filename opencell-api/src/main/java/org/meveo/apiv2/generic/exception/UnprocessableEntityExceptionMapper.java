package org.meveo.apiv2.generic.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer();

    @Override
    public Response toResponse(UnprocessableEntityException exception) {
        log.error("Unprocessable exception occurred ", exception);
        return Response.status(422).entity(exceptionSerializer.toApiError(exception)).type(MediaType.APPLICATION_JSON)
            .header(Validation.VALIDATION_HEADER, "true").build();
    }
}
