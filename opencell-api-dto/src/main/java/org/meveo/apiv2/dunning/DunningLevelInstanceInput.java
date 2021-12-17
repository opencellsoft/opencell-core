package org.meveo.apiv2.dunning;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningLevelInstanceInput.class)
public interface DunningLevelInstanceInput extends Resource {

    @Nullable
    Integer getSequence();

    Integer getDaysOverdue();
    
    @Nullable
    Resource getPolicyLevel();
    
    @Nullable
    Resource getCollectionPlanStatus();

    @Nullable
    DunningLevelInstanceStatusEnum getLevelStatus();

    Resource getCollectionPlan();

    Resource getDunningLevel();

    List<DunningActionInstanceInput> getActions();
}
