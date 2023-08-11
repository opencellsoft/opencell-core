package org.meveo.apiv2.billing;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableValidateInvoiceDto.class)
public interface ValidateInvoiceDto {


	@Nullable
	@Schema(description = "contain filter for retrieving list of invoices ", example = "filters: { inList id : [id1, id2, ...], status: DRAFT, invoice.id: invoiceId")
	Map<String, Object> getFilters();

	@Default
	@Schema(description = "mode processing by default it's PROCESS_ALL")
	default ProcessingModeEnum getMode() {
		return ProcessingModeEnum.PROCESS_ALL;
	}

	@Default
	@Schema(description = "flag to raise an error if trying to validate a VALIDATED invoice")
	default boolean getFailOnValidatedInvoice() {
		return false;
	}

	@Default
	@Schema(description = "flag to raise an error if trying to validate a CANCELED invoice")
	default boolean getFailOnCanceledInvoice() {
		return false;
	}

	@Default
	@Schema(description = "flag to bypass invoice validation rules")
	default boolean getIgnoreValidationRules() {
		return false;
	}
	
	@Default
	@Schema(description = "flag to generate AO")
	default boolean getGenerateAO() {
		return false;
	}
}
