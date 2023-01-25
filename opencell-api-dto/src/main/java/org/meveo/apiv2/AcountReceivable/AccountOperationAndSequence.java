package org.meveo.apiv2.AcountReceivable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAccountOperationAndSequence.class)
public interface AccountOperationAndSequence {

    @Schema(description = "Matching sequence")
    @Nonnull
    Integer getSequence();

    @Schema(description = "AccountOperation Id")
    @Nonnull
    Long getId();
    

    @Schema(description = "amount to match")
    @Nullable
    BigDecimal getAmountToMatch();
}