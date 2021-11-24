package org.meveo.apiv2.dunning;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRemoveLevelInstanceInput.class)
public interface RemoveLevelInstanceInput {

    List<Long> getLevels();
}
