package org.meveo.service.cpq;

import javax.ejb.Stateless;

import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.service.base.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteProductService extends BusinessService<QuoteProduct> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteProductService.class);
	

	
	public QuoteProduct addNewQuoteProduct(QuoteProduct quoteProduct){
		LOGGER.info("adding new quote product for code {}", quoteProduct.getCode());
		final QuoteProduct tmpQuoteProduct = this.findByCode(quoteProduct.getCode());
		if(tmpQuoteProduct != null)
			throw new EntityAlreadyExistsException(QuoteProduct.class, tmpQuoteProduct.getCode());
		
		/*final QuoteProduct quoteProduct = new QuoteProduct();
		quoteProduct.setQuote(quote);
		quoteProduct.setQuoteVersion(quoteVersion);
		quoteProduct.setQuoteCustomer(customerService);
		quoteProduct.setOfferComponent(offerComponent);
		quoteProduct.setProduct(product);
		quoteProduct.setQuantity(quantity);
		quoteProduct.setCode(code);
		quoteProduct.setDescription(label);*/
		
		this.create(quoteProduct);
		return quoteProduct;
	}
}
