package org.meveo.apiv2.billing;

import static java.lang.Boolean.FALSE;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.meveo.apiv2.models.Resource;

import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCancellationInput.class)
public interface CancellationInput extends Resource {

    @Schema(description = "Filters on RT")
    Map<String, Object> getFilters();

    @Default
    @Schema(description = "Fails on incorrect status")
    default Boolean getFailOnIncorrectStatus() {
        return FALSE;
    }

    @Default
    @Schema(description = "Returns RT list")
    default Boolean getReturnRTs() {
        return FALSE;
    }
}
