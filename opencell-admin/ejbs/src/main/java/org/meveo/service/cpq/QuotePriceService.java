package org.meveo.service.cpq;

import java.math.BigDecimal;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.enums.OneShotTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuoteItem;
import org.meveo.model.quote.QuotePrice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.QuotePriceException;
import org.meveo.service.quote.QuoteItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class QuotePriceService extends PersistenceService<QuotePrice> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuotePriceService.class);

	private final static String QUOTE_ITEM_MISSING = "quote item for code %s is missing";
	private final static String QUOTE_PRICE_EXIST_ALREADY = "quote price for code %s exist already";
	
	@Inject
	private QuoteItemService quoteItemService;
	
	/**
	 * adding new Quote Item
	 * @param code
	 * @param description
	 * @param codeQuoteItem
	 * @param chargeCode
	 * @param priceType
	 * @param recurenceDuration
	 * @param recurencePeriodicity
	 * @param overCharge
	 * @param oneShotType
	 * @param mapOfParamAndDimMatrix will contains information for 4 keys for params (param1 & param2 & param3 & param4) and 3 dimMatrix like (dim1Matrix, dim2Matrix, dim3Matrix)
	 * @param priceMatrix
	 * @param usageCode
	 * @param quantity
	 * @param unitePriceWithoutTax
	 * @param priceWithoutTax
	 * @param taxCode
	 * @param taxRate
	 * @param priceWithTax
	 * @return
	 * @throws QuotePriceException if quote item is null & code of the new quote price exist already
	 */
	public QuotePrice addNewQuotePrice(String code, String description, String codeQuoteItem, String chargeCode, PriceTypeEnum priceType,
										int recurenceDuration, int recurencePeriodicity, boolean overCharge, 
										OneShotTypeEnum oneShotType, Map<String,String> mapOfParamAndDimMatrix, Boolean priceMatrix, 
										String usageCode, BigDecimal quantity, BigDecimal unitePriceWithoutTax, 
										BigDecimal priceWithoutTax, BigDecimal taxCode, int taxRate, BigDecimal priceWithTax) throws QuotePriceException {
		
		LOGGER.info("adding new quote price for quote item code {}", codeQuoteItem);
		
		final QuoteItem item = quoteItemService.findByCode(codeQuoteItem);
		if(item == null)
			throw new QuotePriceException(String.format(QUOTE_ITEM_MISSING, codeQuoteItem));
		if(this.findByCode(code) != null)
			throw new QuotePriceException(String.format(QUOTE_PRICE_EXIST_ALREADY, codeQuoteItem));
		
		final QuotePrice q = new QuotePrice();
		q.setCode(code);
		q.setVersion(1);
		q.setDescription(description);
		q.setQuoteItem(item);
		q.setChargeCode(chargeCode);
		q.setPriceType(priceType);
		q.setRecurenceDuration(recurenceDuration);
		q.setRecurencePeriodicity(recurencePeriodicity);
		q.setOverCharge(overCharge);
		q.setOneShotType(oneShotType);
		q.setParam1(mapOfParamAndDimMatrix.get("param1"));
		q.setParam2(mapOfParamAndDimMatrix.get("param2"));
		q.setParam3(mapOfParamAndDimMatrix.get("param3"));
		q.setParam4(mapOfParamAndDimMatrix.get("param4"));
		q.setPriceMatrix(priceMatrix);
		q.setDim1Matrix(mapOfParamAndDimMatrix.get("dim1Matrix"));
		q.setDim2Matrix(mapOfParamAndDimMatrix.get("dim2Matrix"));
		q.setDim3Matrix(mapOfParamAndDimMatrix.get("dim3Matrix"));
		q.setUsageCode(usageCode);
		q.setQuantity(quantity);
		q.setUnitePriceWithoutTax(unitePriceWithoutTax);
		q.setPriceWithoutTax(priceWithoutTax);
		q.setTaxCode(taxCode);
		q.setTaxRate(taxRate);
		q.setPriceWithTax(priceWithTax);
		
		this.create(q);
		return q;
	}

	public QuotePrice findByCode(String code) {
		try {
			return (QuotePrice) this.getEntityManager()
										.createNamedQuery("QuotePrice.findByCode")
											.setParameter("code", code)
												.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
}
