package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningCollectionPlan.class)
public interface DunningCollectionPlan extends Resource {

    @Schema(description = "Collection plan billing Account")
    @Nullable
    Resource getCollectionPlanBillingAccount();

    @Schema(description = "Indicate collection plan payment method")
    @Nullable
    Resource getCollectionPlanPaymentMethod();

    @Schema(description = "Indicate collection plan pause reason")
    @Nullable
    Resource getCollectionPlanPauseReason();

    @Schema(description = "Indicate collection plan related policy")
    @Nullable
    Resource getCollectionPlanRelatedPolicy();

    @Schema(description = "Indicate collection plan current dunning level")
    @Nullable
    Resource getCollectionPlanCurrentDunningLevel();

    @Schema(description = "Indicate collection plan start date")
    @Nullable
    Date getStartDate();

    @Schema(description = "Indicate collection plan days open")
    @Nullable
    Integer getCollectionPlanDaysOpen();

    @Schema(description = "Indicate collection plan last update")
    @Nullable
    Date getCollectionPlanLastUpdate();

    @Schema(description = "Indicate collection plan due balance")
    @Nullable
    Date getCollectionPlanPausedUntilDate();

    @Schema(description = "Indicate collection plan due balance")
    @Nullable
    BigDecimal getCollectionPlanTotalBalance();

    @Schema(description = "Indicate collection plan aged balance")
    @Nullable
    BigDecimal getCollectionPlanAgedBalance();

    @Schema(description = "Indicate collection plan due balance")
    @Nullable
    BigDecimal getCollectionPlanDueBalance();

    @Schema(description = "Indicate collection plan disputed balance")
    @Nullable
    BigDecimal getCollectionPlanDisputedBalance();

    @Schema(description = "Indicate Retry payment on resume date")
    @Nullable
    Boolean getRetryPaymentOnResumeDate();

    @Schema(description = "Indicate collection plan stop date")
    @Nullable
    Date getStopDate();

    @Schema(description = "Indicate collection plan dunning level")
    @Nullable
    Resource getPolicyLevel();

    @Schema(description = "Indicate collection plan status")
    @Nullable
    Resource getCollectionPlanStatus();

    @Schema(description = "Indicate collection plan sequence")
    @Nullable
    Integer getCollectionPlanSequence();
}