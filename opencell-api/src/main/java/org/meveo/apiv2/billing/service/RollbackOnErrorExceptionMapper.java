package org.meveo.apiv2.billing.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollbackOnErrorExceptionMapper implements ExceptionMapper<RollbackOnErrorException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(RollbackOnErrorException exception) {
        log.error("RollbackOnError exception occurred ", exception);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(exception.getCdrListResult())
                .type(MediaType.APPLICATION_JSON)
                .header(Validation.VALIDATION_HEADER, "true")
            .build();
    }
}
