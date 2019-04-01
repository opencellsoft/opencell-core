package org.meveo.apiv2.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.models.ImmutableApiException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getCause())
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }


    private ApiException toApiError(Exception exception) {
        return ImmutableApiException.builder()
                .code("400")
                .details(exception.getMessage() != null ? exception.getMessage() : getStackTrace(exception.getStackTrace()))
                .build();
    }

    private String getStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

}
