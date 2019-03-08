package org.meveo.apiv2.exception;

import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error : " + getErrorMessage(exception))
                .type(MediaType.TEXT_PLAIN).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }

    private String getErrorMessage(Exception exception) {
        return exception.getMessage() != null ? exception.getMessage() : getStackTrace(exception.getStackTrace());
    }

    private String getStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
