package org.meveo.apiv2.quote;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.api.dto.cpq.QuoteAttributeDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableQuoteOffer.class)
public interface QuoteOffer extends Resource{

    @Schema(description = "Discount plan attached to quote offer")
    @Nullable
	Resource getDiscountPlan();

    @Schema(description = "Offer template id")
    @Nonnull
	Resource getOfferTemplate();

    @Schema(description = "billing account")
    @Nullable
	Resource getBillableAccount();

    @Schema(description = "quote version")
    @Nonnull
	Resource getQuoteVersion();

    @Schema(description = "list of quote products")
    @Nullable
	List<QuoteProductDTO> getQuoteProduct();

    @Schema(description = "quote lot")
    @Nullable
	Resource getQuoteLot();

    @Schema(description = "contract code")
    @Nullable
	String getContractCode();

    @Schema(description = "postion of the quote item")
    @Nullable
	Integer getPosition();

    @Schema(description = "sequence of the quote offer")
    @Nullable
	Integer getSequence();

    @Schema(description = "list of the quote attributes")
    @Nullable
	List<QuoteAttributeDTO> getQuoteAttributes();
    
    @Schema(description = "delivery date")
    @Nullable
	Date getDeliveryDate();
	
//	List<QuotePrice> getQuotePrices();
}
