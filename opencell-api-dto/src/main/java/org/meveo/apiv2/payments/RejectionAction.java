package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRejectionAction.class)
public interface RejectionAction extends Resource {

    @Nullable
    @Schema(description = "Payment rejection action description")
    String getDescription();

    @Nullable
    @Schema(description = "Payment rejection action sequence")
    Integer getSequence();

    @Nullable
    @Schema(description = "Payment rejection action associated script")
    Resource getScriptInstance();
}
