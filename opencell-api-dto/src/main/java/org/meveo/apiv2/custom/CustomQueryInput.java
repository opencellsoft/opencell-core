package org.meveo.apiv2.custom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.model.custom.query.QueryVisibilityEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCustomQueryInput.class)
public interface CustomQueryInput {

    @Schema(description = "Custom query code")
    String getQueryName();

    @Nullable
    @Schema(description = "Custom query description")
    String getQueryDescription();

    @Schema(description = "Custom query description")
    String getTargetEntity();

    @Schema(description = "Custom query description")
    QueryVisibilityEnum getVisibility();

    @Nullable
    @Schema(description = "Custom query description")
    List<String> getFields();

    @Nullable
    @Schema(description = "Custom query description")
    Map<String, String> getFilters();
}