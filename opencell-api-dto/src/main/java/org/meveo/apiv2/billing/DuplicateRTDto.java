package org.meveo.apiv2.billing;

import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;
<<<<<<< HEAD
import org.immutables.value.Value.Default;
=======
>>>>>>> 7d02c8df66 (INTRD-14264)

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDuplicateRTDto.class)
public interface DuplicateRTDto {


	@Nullable
	@Schema(description = "contain filter for retrieving list of ratedTransaction ", example = "filters: { inList id : [id1, id2, ...], status: BILLED, invoiceLine.invoice.id: invoiceId")
	Map<String, Object> getFilters();

<<<<<<< HEAD
	@Default
=======
	@Nullable
>>>>>>> 7d02c8df66 (INTRD-14264)
	@Schema(description = "mode processing by default it's PROCESS_ALL")
	default ProcessingModeEnum getMode() {
		return ProcessingModeEnum.PROCESS_ALL;
	}

<<<<<<< HEAD
	@Default
=======
	@Nullable
>>>>>>> 7d02c8df66 (INTRD-14264)
	@Schema(description = "flag to negate unit and total amounts")
	default boolean getNegateAmount() {
		return false;
	}

<<<<<<< HEAD
	@Default
=======
	@Nullable
>>>>>>> 7d02c8df66 (INTRD-14264)
	@Schema(description = "return number of processed/success/fails ids")
	default boolean getReturnRts() {
		return false;
	}
}
