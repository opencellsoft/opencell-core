package org.meveo.apiv2.dunning;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCollectionPlanStatus.class)
public interface CollectionPlanStatus extends Resource {

    @Schema(description = "dunning setting id")
    @Nullable
	Resource  getDunningSettings();

    @Schema(description = "indacte language used in the collection")
    @Nullable
    List<LanguageDescriptionDto> getLanguage();

    @Schema(description = "indicate the status used in the collection")
    @Nullable
    String getStatus();

    @Schema(description = "indicate context for the collection")
    @Nullable
    String getContext();
}
