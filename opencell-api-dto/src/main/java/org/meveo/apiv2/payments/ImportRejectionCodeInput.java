package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableImportRejectionCodeInput.class)
public interface ImportRejectionCodeInput {

    @Nullable
    @Value.Default
    default RejectionCodeImportMode getMode() {
        return RejectionCodeImportMode.UPDATE;
    }

    @Nullable
    @Value.Default
    default Boolean getIgnoreLanguageErrors() {
        return Boolean.TRUE;
    }

    @Nullable
    String getBase64csv();
}
