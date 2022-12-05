package org.meveo.apiv2.generic.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(UnprocessableEntityException exception) {
        log.error("Unprocessable exception occurred ", exception);
        ActionStatus result = new ActionStatus();
        result.setStatus(ActionStatusEnum.FAIL);
        result.setMessage(exception.getMessage());
        return Response.status(exception.getResponse().getStatus()).entity(result).type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true").build();
    }
}
