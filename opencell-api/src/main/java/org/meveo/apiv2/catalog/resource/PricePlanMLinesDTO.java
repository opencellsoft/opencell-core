package org.meveo.apiv2.catalog.resource;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePricePlanMLinesDTO.class)
public interface PricePlanMLinesDTO extends Resource {

    @Schema(description = "price plan matrix code")
    String getPricePlanMatrixCode();

    @Schema(description = "price plan matrix version")
    Integer getPricePlanMatrixVersion();
    
    @Schema(description = "data")
    byte[] getData();
    
}