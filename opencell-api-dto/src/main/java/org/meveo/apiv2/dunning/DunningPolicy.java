package org.meveo.apiv2.dunning;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.dunning.DunningDetermineLevelBy;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningPolicy.class)
public interface DunningPolicy extends Resource {

    @Schema(description = "dunning policy name")
    @Nullable
    String getPolicyName();

    @Schema(description = "dunning policy description")
    @Nullable
    String getPolicyDescription();

    @Schema(description = "Interest for delay sequence")
    @Nullable
    Integer getInterestForDelaySequence();

    @Value.Default
    @Schema(description = "min balance trigger")
    @Nullable
    default Double getMinBalanceTrigger() {
        return 0.0;
    }

    @Schema(description = "Min balance trigger currency")
    @Nullable
    Resource getMinBalanceTriggerCurrency();

    @Schema(description = "Determine level by")
    @Nullable
    DunningDetermineLevelBy getDetermineLevelBy();

    @Value.Default
    @Schema(description = "include due invoices in threshold")
    @JsonProperty("includeDueInvoicesInThreshold")
    default Boolean isIncludeDueInvoicesInThreshold() { return TRUE; }

    @Value.Default
    @Schema(description = "include pay reminder")
    @JsonProperty("includePayReminder")
    default Boolean isIncludePayReminder() { return FALSE; }

    @Schema(description = "Attach invoices to emails")
    @JsonProperty("attachInvoicesToEmails")
    @Nullable
    Boolean isAttachInvoicesToEmails();

    @Schema(description = "Policy priority")
    @Nullable
    Integer getPolicyPriority();

    @Value.Default
    @Schema(description = "is default policy")
    @JsonProperty("isDefaultPolicy")
    default Boolean isDefaultPolicy() { return FALSE; }

    @Value.Default
    @Schema(description = "is policy is activated")
    @JsonProperty("isActivePolicy")
    default Boolean isActivePolicy() { return TRUE; }

    @Schema(description = "Dunning policy levels")
    @Nullable
    List<DunningPolicyLevel> getDunningPolicyLevels();

    @Schema(description = "Dunning policy rules")
    @Nullable
    List<Resource> getDunningPolicyRules();
}