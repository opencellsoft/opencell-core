package org.meveo.apiv2.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.istack.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ImmutableSuccessResponse.class)
public interface SuccessResponse {
    String getStatus();
    @Nullable
    String getMessage();
}