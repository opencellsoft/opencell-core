package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningMassSwitchInput.class)
public interface DunningMassSwitchInput {

    @Schema(description = "Collection plan list to check")
    @Nullable
    List<Resource> getCollectionPlans();

    @Schema(description = "Dunning policy to use for check")
    @Nullable
    Resource getPolicy();
}