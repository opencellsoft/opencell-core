package org.meveo.service.cpq;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteCustomerService;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.QuoteProductException;
import org.meveo.service.quote.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteProductService extends PersistenceService<QuoteProduct> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteProductService.class);
	
	private static final String QUOTE_PRODUCT_ALREADY_EXIST = "Quote product for code %s, exist already";

	
	public QuoteProduct addNewQuoteProduct(String code, String label, Quote quote, QuoteVersion quoteVersion, QuoteCustomerService customerService, 
												OfferComponent offerComponent, Product product, BigDecimal quantity) throws QuoteProductException {
		LOGGER.info("adding new quote product for code {}", code);
		if(this.findByCode(code) != null)
			throw new QuoteProductException(String.format(QUOTE_PRODUCT_ALREADY_EXIST, code));
		
		final QuoteProduct quoteProduct = new QuoteProduct();
		quoteProduct.setQuote(quote);
		quoteProduct.setQuoteVersion(quoteVersion);
		quoteProduct.setQuoteCustomer(customerService);
		quoteProduct.setOfferComponent(offerComponent);
		quoteProduct.setProduct(product);
		quoteProduct.setQuantity(quantity);
		quoteProduct.setCode(code);
		quoteProduct.setDescription(label);
		
		this.create(quoteProduct);
		return quoteProduct;
	}

	public QuoteProduct findByCode(String code) {
		try {
			return (QuoteProduct) this.getEntityManager()
										.createNamedQuery("QuoteProduct.findByCode")
											.setParameter("code", code)
												.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
}
