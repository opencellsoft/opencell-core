package org.meveo.apiv2.quote.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.cpq.QuoteArticleLineService;
import org.meveo.service.cpq.QuoteAttributeService;
import org.meveo.service.cpq.QuoteProductService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.quote.QuoteOfferService;

public class QuoteOfferApiService {

	@Inject	private QuoteOfferService quoteOfferService;
	@Inject	private QuoteVersionService quoteVersionService;
	@Inject	private QuoteProductService quoteProductService;
	@Inject	private QuoteAttributeService quoteAttributeService;
    @Inject private QuoteArticleLineService articleLineService;
	
	public Optional<QuoteOffer> findById(Long quoteOfferId) {
		return Optional.ofNullable(quoteOfferService.findById(quoteOfferId));
	}
	
	public QuoteOffer duplicate(QuoteOffer quoteOffer, String quoteCode, Integer quoteCurrentVersion) {
		var quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, quoteCurrentVersion); 
		if(quoteVersion == null) {
			throw new EntityDoesNotExistsException(QuoteVersion.class, "("+quoteCode+", "+quoteCurrentVersion+")");
		}
		quoteOffer.getQuoteProduct().size();
		quoteOffer.getQuoteProduct().forEach(quoteProduct -> {
			quoteProduct.getQuoteAttributes().size();
			quoteProduct.getQuoteArticleLines().size(); 
		});
		quoteOffer.getQuoteAttributes().size();
		
		var quoteProducts = new ArrayList<QuoteProduct>(quoteOffer.getQuoteProduct());
		var quoteAttributes = new ArrayList<QuoteAttribute>(quoteOffer.getQuoteAttributes());
		
		var duplicate = new QuoteOffer(quoteOffer);
		duplicate.setQuoteVersion(quoteVersion);
		duplicate.setSequence(quoteOffer.getSequence());
		
		quoteOfferService.detach(quoteOffer);
		
		duplicate.setQuoteProduct(new ArrayList<QuoteProduct>());
		duplicate.setQuoteAttributes(new ArrayList<QuoteAttribute>());
		
		quoteOfferService.create(duplicate);
		
		duplicateQuoteProduct(duplicate, quoteProducts);
		duplicateQuoteAttribute(null, duplicate, quoteAttributes);
		return duplicate;
	}
	
	private void duplicateQuoteProduct(QuoteOffer quoteOffer, List<QuoteProduct> quoteProducts) {
		if(quoteProducts != null) {
			for (QuoteProduct quoteProduct : quoteProducts) {
				var duplicate = new QuoteProduct(quoteProduct);
				quoteProductService.detach(quoteProduct);
				
				var quoteAttribute = new ArrayList<>(quoteProduct.getQuoteAttributes());
				var quoteArticleLine = new ArrayList<>(quoteProduct.getQuoteArticleLines());
				
				duplicate.setQuoteOffer(quoteOffer);
				duplicate.setQuoteVersion(quoteOffer.getQuoteVersion());
				duplicate.setQuoteAttributes(new ArrayList<QuoteAttribute>());
				duplicate.setQuoteArticleLines(new ArrayList<QuoteArticleLine>());
				
				quoteProductService.create(duplicate);
				
				duplicateQuoteAttribute(duplicate, quoteOffer, quoteAttribute);
				duplicateArticleLine(quoteProduct, quoteArticleLine, quoteOffer.getQuoteVersion());
				
				quoteOffer.getQuoteProduct().add(duplicate);
				
			}
		}
	}
	
	private void duplicateQuoteAttribute(QuoteProduct quoteProduct, QuoteOffer quoteOffer,  List<QuoteAttribute> quoteAttributes) {
		if(quoteAttributes != null) {
			for (QuoteAttribute quoteAttribute : quoteAttributes) { 
				var duplicate = new QuoteAttribute(quoteAttribute);
				quoteAttributeService.detach(quoteAttribute); 
				
				duplicate.setQuoteOffer(quoteOffer);
				if(quoteProduct != null)
					duplicate.setQuoteProduct(quoteProduct);
				quoteAttributeService.create(duplicate);
				
				quoteOffer.getQuoteAttributes().add(duplicate);
				
			}
		}
	}
	
	
	private void duplicateArticleLine(QuoteProduct quoteProduct, List<QuoteArticleLine> quoteArticleLines, QuoteVersion quoteVersion) {
		if(quoteArticleLines != null) {
			for (QuoteArticleLine quoteArticleLine : quoteArticleLines) {
				final var duplicate = new QuoteArticleLine(quoteArticleLine);
				duplicate.setQuoteProduct(quoteProduct);
				duplicate.setQuoteVersion(quoteVersion);
				
				articleLineService.create(duplicate);
				quoteProduct.getQuoteArticleLines().add(duplicate);
			}
		}
	}
	
}
