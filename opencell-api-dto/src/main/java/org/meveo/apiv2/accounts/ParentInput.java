package org.meveo.apiv2.accounts;

import jakarta.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableParentInput.class)
public interface ParentInput {

    @Schema(description = "Parent id")
    @Nullable
    Long getParentId();

    @Schema(description = "Parent code")
    @Nullable
    String getParentCode();
}
