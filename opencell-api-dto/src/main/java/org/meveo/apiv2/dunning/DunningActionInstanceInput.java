package org.meveo.apiv2.dunning;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.DunningActionInstanceStatusEnum;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.ActionTypeEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningActionInstanceInput.class)
public interface DunningActionInstanceInput {

    @Nullable
    ActionTypeEnum getActionType();

    @Nullable
    String getCode();

    @Nullable
    String getDescription();

    @Nullable
    ActionModeEnum getMode();

    @Nullable
    Resource getActionOwner();

    @Nullable
    Resource getDunningAction();

    @Nullable
    String getActionRestult();

    default DunningActionInstanceStatusEnum getActionStatus() {
        return DunningActionInstanceStatusEnum.TO_BE_DONE;
    }

    @Nullable
    Resource getCollectionPlan();

    @Nullable
    Resource getDunningLevelInstance();
}
