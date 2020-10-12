/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.ordering.exception;

import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.models.Cause;
import org.meveo.apiv2.models.ImmutableApiException;
import org.meveo.apiv2.models.ImmutableCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ExceptionSerializer {

    private final String code;

    ExceptionSerializer(String code) {
        this.code = code;
    }

    public ApiException toApiError(Exception exception) {
        final List<Cause>  cause = getCause(exception);
        return ImmutableApiException.builder()
                .code(code)
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
