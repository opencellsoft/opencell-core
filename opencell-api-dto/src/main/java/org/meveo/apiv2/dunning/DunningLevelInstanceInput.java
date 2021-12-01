package org.meveo.apiv2.dunning;

import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.DunningAgent;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningLevelInstance;
import org.meveo.model.dunning.DunningLevelInstanceStatusEnum;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.scripts.ScriptInstance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningLevelInstanceInput.class)
public interface DunningLevelInstanceInput {

    Integer getSequence();

    Integer getDaysOverdue();

    default DunningLevelInstanceStatusEnum getLevelStatus() {
        return DunningLevelInstanceStatusEnum.TO_BE_DONE;
    }

    Resource getCollectionPlan();

    Resource getDunningLevel();

    List<DunningActionInstanceInput> getActions();
}
