package org.meveo.apiv2.dunning;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlan.class)
public interface DunningCollectionPlan extends Resource {

    @Nullable
    Resource getCollectionPlanRelatedPolicy();

    @Nullable
    Resource getInitialCollectionPlan();

    @Nullable
    Resource getCollectionPlanBillingAccount();

    @Nullable
    Resource getCollectionPlanRelatedInvoice();

    @Nullable
    Resource getCollectionPlanPauseReason();

    @Nullable
    Resource getCollectionPlanStopReason();

    @Nullable
    Integer getCollectionPlanCurrentDunningLevelSequence();

    @Nullable
    Date getCollectionPlanStartDate();

    @Nullable
    Integer getCollectionPlanDaysOpen();

    @Nullable
    Date getCollectionPlanCloseDate();

    @Nullable
    Resource getCollectionPlanStatus();

    @Nullable
    Date getCollectionPlanPausedUntilDate();

    @Nullable
    BigDecimal getCollectionPlanBalance();

    @Nullable
    Boolean getRetryPaymentOnResumeDate();

    @Nullable
    List<Resource> getDunningLevelInstances();

    @Nullable
    String getCollectionPlanNextAction();

    @Nullable
    Date getCollectionPlanNextActionDate();

    @Nullable
    String getCollectionPlanLastAction();

    @Nullable
    Date getCollectionPlanLastActionDate();

    @Nullable
    Integer totalDunningLevels();
}