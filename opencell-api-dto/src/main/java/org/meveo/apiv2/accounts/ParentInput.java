package org.meveo.apiv2.accounts;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableParentInput.class)
public interface ParentInput {

    @Schema(description = "Parent id")
    @Nullable
    Long getParentId();

    @Schema(description = "Parent code")
    @Nullable
    String getParentCode();
    
    @Default
    @Schema(description = "flag to forces OPEN  wallet operations rerate")
    default boolean isMarkOpenWalletOperationsToRerate() {
        return false;
    }
}
