package org.meveo.apiv2.AcountReceivable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUnMatchingAccountOperationDetail.class)
public interface UnMatchingAccountOperationDetail {

    @Schema(description = "AccountOperation Id")
    @NotNull
    Long getId();

    @Schema(description = "MatchingAmount Id")
    List<Long> getMatchingAmountIds();
}