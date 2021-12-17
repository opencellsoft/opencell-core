package org.meveo.apiv2.dunning;

import java.util.List;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRemoveLevelInstanceInput.class)
public interface RemoveLevelInstanceInput {

    List<Resource> getLevels();
}
