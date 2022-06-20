package org.meveo.apiv2.settings;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableQuoteSettings.class)
public interface QuoteSettings {

    @Nullable
    Integer getQuoteDefaultValidityDelay();

}
