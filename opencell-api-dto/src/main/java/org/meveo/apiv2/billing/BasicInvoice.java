package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableBasicInvoice.class)
public interface BasicInvoice extends Resource {

	@Schema(description = "The billing account code")
	String getBillingAccountCode();

	@Nullable
	@Schema(description = "The order code for invoice")
	String getOrderCode();

	@Nullable
	@Schema(description = "The Date of the invoice")
	Date getInvoiceDate();

	@Nullable
	@Schema(description = "The due date")
	Date getDueDate();

	@Nullable
	@Schema(description = "The article code for invoice")
	String getArticleCode();

	@Nullable
	@Schema(description = "The label")
	String getLabel();

	@Nullable
	@Schema(description = "The maount with tax")
	BigDecimal getAmountWithTax();
	
	@Nullable
	@Schema(description = "The invoice type code")
	String getInvoiceTypeCode();
	
	@Schema(description = "The comment for the invoice")
	@Nullable
	String getComment();

	@Schema(description = "The Seller")
	@Nullable
	Resource getSeller();

	@Schema(description = "The flag for auto matching")
	@Nullable
	Boolean getAutoMatching();
	
	@Schema(description = "The external purchase order number")
	@Nullable
	String getPurchaseOrder();

}
