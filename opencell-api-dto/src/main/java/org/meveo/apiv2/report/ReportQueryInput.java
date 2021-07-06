package org.meveo.apiv2.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.model.report.query.QueryVisibilityEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableReportQueryInput.class)
public interface ReportQueryInput {

    @Schema(description = "Report query code")
    String getQueryName();

    @Nullable
    @Schema(description = "Report query description")
    String getQueryDescription();

    @Schema(description = "Report query description")
    String getTargetEntity();

    @Schema(description = "Report query description")
    QueryVisibilityEnum getVisibility();

    @Nullable
    @Schema(description = "Report query description")
    List<String> getFields();

    @Nullable
    @Schema(description = "Report query description")
    Map<String, String> getFilters();
}