package org.meveo.apiv2.generic.exception;


import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.models.Cause;
import org.meveo.apiv2.models.ImmutableApiException;
import org.meveo.apiv2.models.ImmutableCause;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ExceptionSerializer {

    private final Response.Status status;

    ExceptionSerializer(Response.Status status) {
        this.status = status;
    }

    public ApiException toApiError(Exception exception) {
        final List<Cause>  cause = getCause(exception);
        return ImmutableApiException.builder()
                .status(status)
                .details(exception.getMessage() != null ? exception.getMessage() : getStackTrace(exception.getStackTrace()))
                .addAllCauses(cause)
                .build();
    }

    private String getStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    private List<Cause> getCause(Exception exception) {
        List<Throwable> causes = new ArrayList<>();
        Throwable nextCause = exception;
        do{
            causes.add(nextCause.getCause());
            nextCause = nextCause.getCause();
        } while (nextCause != null && causes.contains(nextCause));

        return causes.stream()
                .filter(cause -> cause != null && cause.getMessage() != null)
                .map(cause -> ImmutableCause.builder().causeMessage(cause.getMessage()).build())
                .collect(Collectors.toList());
    }
}
