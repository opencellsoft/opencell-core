package org.meveo.apiv2.custom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.custom.query.QueryVisibilityEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCustomQuery.class)
public interface CustomQuery extends Resource {

    @Schema(description = "Custom query code")
    String getCode();

    @Schema(description = "Custom query description")
    @Nullable
    String getDescription();

    @Schema(description = "Custom query target entity")
    String getTargetEntity();

    @Schema(description = "Visibility", example = "Possible value are : PUBLIC, PROTECTED, PRIVATE")
    QueryVisibilityEnum getVisibility();

    @Schema(description = "Custom query fields")
    List<String> getFields();

    @Schema(description = "Custom query filters")
    @Nullable
    Map<String, String> getFilters();

    @Schema(description = "Generated query")
    @Nullable
    String getGeneratedQuery();
}