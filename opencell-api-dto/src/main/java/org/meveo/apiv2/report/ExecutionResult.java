package org.meveo.apiv2.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableReportQuery.class)
public interface ExecutionResult extends Resource {

    @Schema(description = "Report query execution results")
    List<Object> getExecutionResults();

    @Schema(description = "Execution results count")
    long getTotal();
}
