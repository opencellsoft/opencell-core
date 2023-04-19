package org.meveo.apiv2.quote.impl;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.meveo.api.dto.cpq.QuoteAttributeDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.api.dto.cpq.TaxDTO;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.quote.ImmutableQuoteOffer;
import org.meveo.model.cpq.offer.QuoteOffer;

public class QuoteOfferMapper extends ResourceMapper<org.meveo.apiv2.quote.QuoteOffer, QuoteOffer> {

	@Override
	protected org.meveo.apiv2.quote.QuoteOffer toResource(QuoteOffer entity) {
		return ImmutableQuoteOffer.builder()
				.id(entity.getId())
				.discountPlan(createResource(entity.getDiscountPlan()))
				.offerTemplate(createResource(entity.getOfferTemplate()))
				.billableAccount(createResource(entity.getBillableAccount()))
				.quoteVersion(createResource(entity.getQuoteVersion()))
				.quoteLot(createResource(entity.getQuoteLot()))
				.contractCode(entity.getContract() != null ? entity.getContract().getCode() : null)
				.position(entity.getPosition())
				.sequence(entity.getSequence())
				.quoteProduct(entity.getQuoteProduct().stream().map(qp -> new QuoteProductDTO(qp, true, new HashMap<String, TaxDTO>())).collect(Collectors.toList()))
				.quoteAttributes(entity.getQuoteAttributes().stream().map(qa -> new QuoteAttributeDTO(qa)).collect(Collectors.toList()))
				.deliveryDate(entity.getDeliveryDate())
				.build();
	}
	

	@Override
	protected QuoteOffer toEntity(org.meveo.apiv2.quote.QuoteOffer resource) {

		return null;
	}

}
