package org.meveo.apiv2.quote.service;

import java.util.Optional;

import jakarta.inject.Inject;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.catalog.impl.CatalogHierarchyBuilderService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.quote.QuoteOfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteOfferApiService {

	/** Logger. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
	private QuoteOfferService quoteOfferService;
	@Inject
	private QuoteVersionService quoteVersionService;
	@Inject
	CatalogHierarchyBuilderService catalogHierarchyBuilderService;
	
	public Optional<QuoteOffer> findById(Long quoteOfferId) {
		return Optional.ofNullable(quoteOfferService.findById(quoteOfferId));
	}
	
	public QuoteOffer duplicate(QuoteOffer quoteOffer, String quoteCode, Integer quoteCurrentVersion) {
		var quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, quoteCurrentVersion); 
				if(quoteVersion == null) {
					throw new EntityDoesNotExistsException(QuoteVersion.class, "("+quoteCode+", "+quoteCurrentVersion+")");
		 		}
				
				return catalogHierarchyBuilderService.duplicateQuoteOffer(quoteOffer,quoteVersion);	
		
	}
	
	
	
}
