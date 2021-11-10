package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPolicyInput.class)
public interface DunningPolicyInput extends Resource {

    @Schema(description = "dunning policy name")
    @Nullable
    String getPolicyName();

    @Schema(description = "dunning policy description")
    @Nullable
    String getPolicyDescription();

    @Schema(description = "Interest for delay sequence")
    @Nullable
    Integer getInterestForDelaySequence();

    @Schema(description = "min balance trigger")
    @Nullable
    Double getMinBalanceTrigger();

    @Schema(description = "Min balance trigger currency")
    @Nullable
    List<String> getMinBalanceTriggerCurrency();

    @Schema(description = "Determine level by")
    @Nullable
    List<String> getDetermineLevelBy();

    @Nullable
    @Schema(description = "include due invoices in threshold")
    @JsonProperty("includeDueInvoicesInThreshold")
    Boolean isIncludeDueInvoicesInThreshold();

    @Schema(description = "include pay reminder")
    @JsonProperty("includePayReminder")
    @Nullable
    Boolean isIncludePayReminder();

    @Schema(description = "Attach invoices to emails")
    @JsonProperty("attachInvoicesToEmails")
    @Nullable
    Boolean isAttachInvoicesToEmails();

    @Schema(description = "Policy priority")
    @Nullable
    Integer getPolicyPriority();

    @Schema(description = "is default policy")
    @JsonProperty("isDefaultPolicy")
    @Nullable
    Boolean isDefaultPolicy();

    @Schema(description = "is policy is activated")
    @JsonProperty("isActivePolicy")
    @Nullable
    Boolean isActivePolicy();

    @Schema(description = "Dunning policy levels")
    @Nullable
    List<DunningPolicyLevel> getDunningLevels();

    @Schema(description = "Dunning policy rules")
    @Nullable
    List<Resource> getDunningPolicyRules();
}