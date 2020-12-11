package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableLanguageDescription.class)
public interface LanguageDescription extends Resource {

    String getLanguageDescriptionCode();

    String getDescription();
}
