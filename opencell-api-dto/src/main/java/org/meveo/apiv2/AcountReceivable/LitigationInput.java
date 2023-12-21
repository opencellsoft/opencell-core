package org.meveo.apiv2.AcountReceivable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import javax.annotation.Nullable;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableLitigationInput.Builder.class)
public interface LitigationInput {

    @Schema(description = "Litigation reason to record on account operation")
    @Nullable
    String getLitigationReason();
}
