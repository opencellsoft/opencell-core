package org.meveo.apiv2.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.model.report.query.QueryVisibilityEnum;
import org.meveo.model.report.query.SortOrderEnum;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableReportQueryInput.class)
public interface ReportQueryInput {

    @Nullable
    @Schema(description = "Report query code")
    String getQueryName();

    @Nullable
    @Schema(description = "Report query description")
    String getQueryDescription();

    @Nullable
    @Schema(description = "Report query description")
    String getTargetEntity();

    @Nullable
    @Schema(description = "Report query description")
    QueryVisibilityEnum getVisibility();

    /**
     * @deprecated use instead advancedQuery
     */
    @Nullable
    @Schema(description = "Report query description")
    @Deprecated
    List<String> getGenericFields();

    /**
     * @deprecated use instead advancedQuery
     */
    @Nullable
    @Schema(description = "Report query description")
    @Deprecated
    Map<String, Object> getFilters();

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
    @Schema(description = "Report query emails")
    List<String> getEmails();

    @Nullable
    @Schema(description = "Report query aliases")
    Map<String, String> getAliases();

    @Schema(description = "Report query - Advanced Query")
    @Nullable
    Map<String, Object> getAdvancedQuery();
}
