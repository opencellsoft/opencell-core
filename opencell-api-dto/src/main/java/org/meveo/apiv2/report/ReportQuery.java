package org.meveo.apiv2.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.report.query.QueryVisibilityEnum;
import org.meveo.model.report.query.SortOrderEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableReportQuery.class)
public interface ReportQuery extends Resource {

    @Schema(description = "Report query code")
    @Nullable
    String getCode();

    @Schema(description = "Report query description")
    @Nullable
    String getDescription();

    @Schema(description = "Report query target entity")
    @Nullable
    String getTargetEntity();

    @Schema(description = "Visibility", example = "Possible value are : PUBLIC, PROTECTED, PRIVATE")
    @Nullable
    QueryVisibilityEnum getVisibility();

    /**
     * @deprecated use instead advancedQuery
     */
    @Schema(description = "Report query fields")
    @Nullable
    @Deprecated
    List<String> getFields();

    /**
     * @deprecated use instead advancedQuery
     */
    @Schema(description = "Report query filters")
    @Nullable
    @Deprecated
    Map<String, Object> getFilters();

    @Schema(description = "Generated query")
    @Nullable
    String getGeneratedQuery();

    /**
     * @deprecated use instead advancedQuery
     */
    @Nullable
    @Schema(description = "Sort by")
    @Deprecated
    String getSortBy();

    /**
     * @deprecated use instead advancedQuery
     */
    @Nullable
    @Schema(description = "Sort order", example = "Possible value are : DESCENDING, ASCENDING")
    @Deprecated
    SortOrderEnum getSortOrder();

    @Nullable
    @Schema(description = "Report query Owner name")
    String getOwnerName();

    @Nullable
    @Schema(description = "Report query fields' aliases")
    Map<String, String> getAliases();

    @Schema(description = "Report query - Advanced query")
    @Nullable
    Map<String, Object> getAdvancedQuery();
}
