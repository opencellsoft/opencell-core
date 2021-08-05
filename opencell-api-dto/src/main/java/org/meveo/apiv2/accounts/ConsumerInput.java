package org.meveo.apiv2.accounts;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableConsumerInput.class)
public interface ConsumerInput {

    @Schema(description = "Consumer id")
    @Nullable
    Long getConsumerId();

    @Schema(description = "Consumer code")
    @Nullable
    String getConsumerCode();
}
