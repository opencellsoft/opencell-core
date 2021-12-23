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
	Resource getRelatedPolicy();

	@Nullable
	Resource getInitialCollectionPlan();

	@Nullable
	Resource getBillingAccount();

	@Nullable
	Resource getRelatedInvoice();

	@Nullable
	Resource getPauseReason();

	@Nullable
	Resource getStopReason();

	@Nullable
	Integer getCurrentDunningLevelSequence();

	@Nullable
	Date getStartDate();

	@Nullable
	Integer getDaysOpen();

	@Nullable
	Date getCloseDate();

	@Nullable
	Resource getStatus();

	@Nullable
	Date getPausedUntilDate();

	@Nullable
	BigDecimal getBalance();

	@Nullable
	Boolean getRetryPaymentOnResumeDate();

	@Nullable
	List<Resource> getDunningLevelInstances();

	@Nullable
	String getNextAction();

	@Nullable
	Date getNextActionDate();

	@Nullable
	String getLastAction();

	@Nullable
	Date getLastActionDate();

	@Nullable
	Integer getTotalDunningLevels();

	@Nullable
	String getCollectionPlanNumber();
}