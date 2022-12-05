package org.meveo.apiv2.settings;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableGlobalSettingsInput.class)
public interface GlobalSettingsInput extends Resource {

    @Nonnull
    QuoteSettings getQuoteSettings();

    @Nullable
    Dunning getDunning();
}
