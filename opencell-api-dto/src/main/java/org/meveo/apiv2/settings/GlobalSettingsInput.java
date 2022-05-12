package org.meveo.apiv2.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
