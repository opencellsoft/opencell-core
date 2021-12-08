package org.meveo.apiv2.dunning;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUpdateLevelInstanceInput.class)
public interface UpdateLevelInstanceInput {

    @Nullable
    Integer getDaysOverdue();

    @Nullable
    DunningLevelInstanceStatusEnum getLevelStatus();
    
    @Nullable
    List<DunningActionInstanceInput> getActions();
}
