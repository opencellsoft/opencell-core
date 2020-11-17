package org.meveo.service.cpq;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.quote.QuoteItem;
import org.meveo.model.quote.QuotePrice;
import org.meveo.service.base.BusinessService;
import org.meveo.service.quote.QuoteItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class QuotePriceService extends BusinessService<QuotePrice> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuotePriceService.class);

	
	@Inject
	private QuoteItemService quoteItemService;
	
	/**
	 * adding new Quote Item
	 * @param quotePrice
	 * @param codeQuoteItem
	 * @param mapOfParamAndDimMatrix
	 * @return
	 * @throws QuotePriceException if quote item is null & code of the new quote price exist already
	 */
	public QuotePrice addNewQuotePrice(QuotePrice quotePrice, String codeQuoteItem, Map<String,String> mapOfParamAndDimMatrix ) {
		
		LOGGER.info("adding new quote price for quote item code {}", codeQuoteItem);
		
		final QuoteItem item = quoteItemService.findByCode(codeQuoteItem);
		if(item == null)
			throw new EntityDoesNotExistsException(QuoteItem.class, codeQuoteItem);
		final QuotePrice tmpQuotePrice = this.findByCode(quotePrice.getCode());
		if(tmpQuotePrice != null)
			throw new EntityAlreadyExistsException(QuotePrice.class, quotePrice.getCode());
		
		/*final QuotePrice q = new QuotePrice();
		q.setCode(code);
		q.setVersion(1);
		q.setDescription(description);
		q.setQuoteItem(item);
		q.setChargeCode(chargeCode);
		q.setPriceType(priceType);
		q.setRecurenceDuration(recurenceDuration);
		q.setRecurencePeriodicity(recurencePeriodicity);
		q.setOverCharge(overCharge);
		q.setOneShotType(oneShotType);*/
		quotePrice.setParam1(mapOfParamAndDimMatrix.get("param1"));
		quotePrice.setParam2(mapOfParamAndDimMatrix.get("param2"));
		quotePrice.setParam3(mapOfParamAndDimMatrix.get("param3"));
		quotePrice.setParam4(mapOfParamAndDimMatrix.get("param4"));
		//q.setPriceMatrix(priceMatrix);
		quotePrice.setDim1Matrix(mapOfParamAndDimMatrix.get("dim1Matrix"));
		quotePrice.setDim2Matrix(mapOfParamAndDimMatrix.get("dim2Matrix"));
		quotePrice.setDim3Matrix(mapOfParamAndDimMatrix.get("dim3Matrix"));
		/*q.setUsageCode(usageCode);
		q.setQuantity(quantity);
		q.setUnitePriceWithoutTax(unitePriceWithoutTax);
		q.setPriceWithoutTax(priceWithoutTax);
		q.setTaxCode(taxCode);
		q.setTaxRate(taxRate);
		q.setPriceWithTax(priceWithTax);*/
		
		this.create(quotePrice);
		return quotePrice;
	}
}
