package org.meveo.apiv2.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunning.class)
public interface Dunning {
    @Nonnull
    Boolean getActivateDunning();
}
