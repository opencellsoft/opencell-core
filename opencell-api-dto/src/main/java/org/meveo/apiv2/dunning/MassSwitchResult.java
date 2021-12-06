package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableMassSwitchResult.class)
public interface MassSwitchResult {

    @Schema(description = "Collection plan list total")
    @Nullable
    Long getTotal();

    @Schema(description = "Collection plan list eligible for switch")
    @Nullable
    CheckSwitchResult getCanBeSwitched();

    @Schema(description = "Collection plan list not eligible for switch")
    @Nullable
    CheckSwitchResult getCanNotBeSwitched();
}