package org.meveo.apiv2.standardReport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAgedReceivablesDto.class)
public interface AgedReceivablesDto extends Resource {

	@Schema(description = "Indicate the customer account code")
	@Nullable
	GenericPagingAndFiltering getSearchConfig();

	@Schema(description = "Indicate the customer account code")
	@Nullable
	String getCustomerAccountCode();

	@Schema(description = "Indicate the customer account description")
	@Nullable
	String getCustomerAccountDescription();

	@Schema(description = "Indicate the seller description")
	@Nullable
	String getSellerDescription();

	@Schema(description = "Indicate the seller code")
	@Nullable
	String getSellerCode();

	@Schema(description = "Indicate the invoice number")
	@Nullable
	String getInvoiceNumber();

	@Schema(description = "Indicate the step in days")
	@Nullable
	Integer getStepInDays();

	@Schema(description = "Indicate the number of periods")
	@Nullable
	Integer getNumberOfPeriods();

	@Schema(description = "Indicate the trading currency")
	@Nullable
	String getTradingCurrency();

	@Schema(description = "Indicate the functional currency")
	@Nullable
	String getFunctionalCurrency();

	@Schema(description = "Indicate the start date")
	@Nullable
	Date getStartDate();

	@Schema(description = "Indicate the start due date")
	@Nullable
	Date getStartDueDate();

	@Schema(description = "Indicate the end due date")
	@Nullable
	Date getEndDueDate();
}

