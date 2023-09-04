package org.meveo.apiv2.billing;

import static java.lang.Boolean.FALSE;
import static org.immutables.value.Value.Default;
import static org.immutables.value.Value.Immutable;
import static org.immutables.value.Value.Style;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableExceptionalBillingRun.class)
public interface ExceptionalBillingRun extends Resource {

    @Nullable
    @Schema(description = "Billing run process type")
    BillingProcessTypesEnum getBillingRunTypeEnum();

    @Nullable
    @Schema(description = "Invoice date")
    Date getInvoiceDate();

    @Nullable
    @Schema(description = "custom field associated to Billing Run")
    CustomFieldsDto getCustomFields();

    @Nullable
    @Schema(description = "Billing run collection date")
    Date getCollectionDate();

    @Default
    @Schema(description = "Skip validation script")
    @JsonProperty("skipValidationScript")
    default Boolean isSkipValidationScript() {
        return FALSE;
    }

    @Nullable
    @Schema(description = "reject auto action")
    BillingRunAutomaticActionEnum getRejectAutoAction();

    @Nullable
    @Schema(description = "Suspect auto action")
    BillingRunAutomaticActionEnum getSuspectAutoAction();

    @Schema(description = "Filters on RT")
    Map<String, Object> getFilters();
    
    @Nullable
    String getInvoiceType();

    @Nullable
    @Default
    @Schema(description = "Decide whether or not dates should be recomputed at invoice validation")
    @JsonProperty("computeDatesAtValidation")
    default Boolean isComputeDatesAtValidation() {
        return FALSE;
    }

    @Default
    @Schema(description = "Decide if adding invoice lines incrementally or not")
    @JsonProperty("incrementalInvoiceLines")
    default Boolean isIncrementalInvoiceLines() {
        return FALSE;
    }

    @Default
    @Schema(description = "Decide if Report job will be launched automatically at billing run creation")
    @JsonProperty("preReportAutoOnCreate")
    default Boolean isPreReportAutoOnCreate() {
        return FALSE;
    }
}
