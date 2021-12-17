package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAvailablePoliciesList.class)
public interface AvailablePoliciesList {

    @Schema(description = "Available policies")
    List<DunningPolicy> getAvailablePolicies();

    @Schema(description = "Total available policies")
    Integer getTotal();
}