package org.meveo.apiv2.quote.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.quote.ImmutableQuoteOffer;
import org.meveo.apiv2.quote.resource.QuoteOfferResource;
import org.meveo.apiv2.quote.service.QuoteOfferApiService;
import org.meveo.model.cpq.offer.QuoteOffer;

@Interceptors({ WsRestApiInterceptor.class })
public class QuoteOfferResourceImpl implements QuoteOfferResource {

	@Inject
	private QuoteOfferApiService quoteOfferApiService;
	private QuoteOfferMapper mapper = new QuoteOfferMapper();
	

	@Transactional
	@Override
	public Response duplicate(String quoteCode, Integer quoteVersion, Long quoteItemId) {
		return duplicateQuoteOffer(quoteCode, quoteVersion, quoteItemId);
	}
	
	@Transactional
	@Override
	public Response duplicateQuote(String quoteCode, Integer quoteVersion, Long quoteItemId) {
		return duplicateQuoteOffer(quoteCode, quoteVersion, quoteItemId);
	}

	private Response duplicateQuoteOffer(String quoteCode, Integer quoteVersion, Long quoteItemId) {
		var quoteOffer = quoteOfferApiService.findById(quoteItemId).orElseThrow(() -> new EntityDoesNotExistsException(QuoteOffer.class, quoteItemId));
		var duplicate = quoteOfferApiService.duplicate(quoteOffer, quoteCode, quoteVersion);
		return Response.created(LinkGenerator.getUriBuilderFromResource(QuoteOfferResource.class, duplicate.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(duplicate)))
				.build();
	}
	
	private org.meveo.apiv2.quote.QuoteOffer toResourceOrderWithLink( org.meveo.apiv2.quote.QuoteOffer quoteOffer) {
		return ImmutableQuoteOffer.copyOf(quoteOffer)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(QuoteOfferResource.class)
											.withId(quoteOffer.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

}
