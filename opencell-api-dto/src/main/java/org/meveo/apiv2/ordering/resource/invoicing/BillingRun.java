package org.meveo.apiv2.ordering.resource.invoicing;

import static java.lang.Boolean.FALSE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableBillingRun.class)
public interface BillingRun extends Resource {

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

    @Value.Default
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
    Map<String, String> getFilters();
    
    @Nullable
    String getInvoiceType();
}