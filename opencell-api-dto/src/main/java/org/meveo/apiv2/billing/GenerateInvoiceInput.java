package org.meveo.apiv2.billing;

import static java.lang.Boolean.FALSE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.Date;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableGenerateInvoiceInput.class)
public interface GenerateInvoiceInput extends Resource {

    @Nullable
    @Schema(description = "Billing Cycle Type", example = "possible value are: BILLINGACCOUNT, SUBSCRIPTION, ORDER")
    String getTargetType();

    @Nullable
    @Schema(description = "Target entity code")
    String getTargetCode();

    @Nullable
    @Schema(description = "Billing account code")
    String getBillingAccountCode();

    @Schema(description = "Invoicing date")
    Date getInvoicingDate();

    @Nullable
    @Schema(description = " First transaction date")
    Date getFirstTransactionDate();

    @Nullable
    @Schema(description = " Last transaction date")
    Date getLastTransactionDate();

    @Nullable
    @Schema(description = "Order number")
    String getOrderNumber();

    @Nullable
    @Value.Default
    @JsonProperty("generateXML")
    @Schema(description = "Generate XML")
    default Boolean isGenerateXML() {
        return FALSE;
    }

    @Nullable
    @Value.Default
    @JsonProperty("generatePDF")
    @Schema(description = "Generate PDF")
    default Boolean isGeneratePDF() {
        return FALSE;
    }

    @Nullable
    @Value.Default
    @JsonProperty("generateAO")
    @Schema(description = "Generate AO")
    default Boolean isGenerateAO() {
        return FALSE;
    }

    @Nullable
    @Schema(description = "Custom fields")
    CustomFieldsDto getCustomFields();

    @Nullable
    @Value.Default
    @JsonProperty("includeRatedTransactions")
    @Schema(description = "Includes rated transactions in the return value")
    default Boolean isIncludeRatedTransactions() {
        return FALSE;
    }

    @Nullable
    @Schema(description = "Apply mode for invoice minimum rules")
    String getApplyMinimum();

    @Nullable
    @Value.Default
    @Schema(description = "Indicate if the invoice is DRAFT")
    default Boolean getIsDraft() {
        return FALSE;
    }

    @Nullable
    @Value.Default
    @Schema(description = "Indicate if validation is skipped")
    @JsonProperty("skipValidation")
    default Boolean isSkipValidation() {
        return FALSE;
    }

    @Nullable
    @Value.Default
    @Schema(description = "Apply Billing Rules")
    @JsonProperty("applyBillingRules")
    default Boolean isApplyBillingRules() {
        return FALSE;
    }
    
    @Nullable
    @Schema(description = "Filters on RT")
    FilterDto getFilters();
    
    @Nullable
    @Schema(description = "OpenOrder code")
    String getOpenOrderCode();
    
	@Schema(description = "The external purchase order number")
	@Nullable
	String getPurchaseOrder();
    
}