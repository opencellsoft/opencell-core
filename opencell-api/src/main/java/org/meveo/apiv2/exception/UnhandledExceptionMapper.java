package org.meveo.apiv2.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.models.ImmutableApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(Exception exception) {
        log.error("An unhandled exception occurred ", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(toApiError(exception))
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }

    private ApiException toApiError(Exception exception) {
        return ImmutableApiException.builder()
                .code("500")
                .details(exception.getMessage() != null ? exception.getMessage() : getStackTrace(exception.getStackTrace()))
                .build();
    }

    private String getStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
