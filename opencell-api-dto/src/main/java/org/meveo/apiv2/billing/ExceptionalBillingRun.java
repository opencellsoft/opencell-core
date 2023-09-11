package org.meveo.apiv2.billing;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
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
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.DiscountAggregationModeEnum;

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

    @Nullable
    @Default
    @Schema(description = "Do not aggregate Rated transactions to Invoice lines at all")
    @JsonProperty("disableAggregation")
    default Boolean isDisableAggregation() {
        return FALSE;
    }

    @Nullable
    @Default
    @Schema(description = "Aggregate based on accounting article label instead of RT description")
    @JsonProperty("useAccountingArticleLabel")
    default Boolean isUseAccountingArticleLabel() {
        return FALSE;
    }

    @Nullable
    @Default
    @Schema(description = "Aggregate by date option")
    @JsonProperty("dateAggregation")
    default DateAggregationOption getDateAggregation() {
        return DateAggregationOption.NO_DATE_AGGREGATION;
    }

    @Nullable
    @Default
    @Schema(description = "Aggregate per unit amount")
    @JsonProperty("aggregateUnitAmounts")
    default Boolean isAggregateUnitAmounts() {
        return TRUE;
    }

    @Nullable
    @Default
    @Schema(description = "If TRUE, aggregation will ignore subscription field (multiple subscriptions will be aggregated together)")
    @JsonProperty("ignoreSubscriptions")
    default Boolean isIgnoreSubscriptions() {
        return TRUE;
    }

    @Nullable
    @Default
    @Schema(description = "If TRUE, aggregation will ignore order field (multiple orders will be aggregated together)")
    @JsonProperty("ignoreOrders")
    default Boolean isIgnoreOrders() {
        return TRUE;
    }

    @Nullable
    @Default
    @Schema(description = "Use incremental mode in invoice lines or not")
    @JsonProperty("discountAggregation")
    default DiscountAggregationModeEnum getDiscountAggregation() {
        return DiscountAggregationModeEnum.FULL_AGGREGATION;
    }

    @Default
    @Schema(description = "Decide if adding invoice lines incrementally or not")
    @JsonProperty("incrementalInvoiceLines")
    default Boolean isIncrementalInvoiceLines() {
        return FALSE;
    }
}