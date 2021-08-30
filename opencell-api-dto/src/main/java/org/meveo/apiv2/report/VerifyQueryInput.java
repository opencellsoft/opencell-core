package org.meveo.apiv2.report;

import javax.annotation.Nullable;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.model.report.query.QueryVisibilityEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableVerifyQueryInput.class)
public interface VerifyQueryInput {

    @Nullable
    @Schema(description = "Report query code")
    String getQueryName();

    @Nullable
    @Schema(description = "Report query description")
    QueryVisibilityEnum getVisibility();
}