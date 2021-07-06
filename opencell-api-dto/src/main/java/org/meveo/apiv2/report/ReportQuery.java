package org.meveo.apiv2.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.report.query.QueryVisibilityEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableReportQuery.class)
public interface ReportQuery extends Resource {

    @Schema(description = "Report query code")
    String getCode();

    @Schema(description = "Report query description")
    @Nullable
    String getDescription();

    @Schema(description = "Report query target entity")
    String getTargetEntity();

    @Schema(description = "Visibility", example = "Possible value are : PUBLIC, PROTECTED, PRIVATE")
    QueryVisibilityEnum getVisibility();

    @Schema(description = "Report query fields")
    List<String> getFields();

    @Schema(description = "Report query filters")
    @Nullable
    Map<String, String> getFilters();

    @Schema(description = "Generated query")
    @Nullable
    String getGeneratedQuery();
}