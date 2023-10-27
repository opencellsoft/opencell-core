package org.meveo.common;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteVersion;

public class UtilsDto {


    public static Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType, PriceLevelEnum priceLevelEnum, 
    													QuoteVersion quoteVersion, QuoteOffer quoteOffer) {
    	if(pricesPerType == null || pricesPerType.get(key) == null) return Optional.empty();

        QuotePrice quotePrice = new QuotePrice();
        quotePrice.setPriceTypeEnum(key);
        quotePrice.setPriceLevelEnum(priceLevelEnum);
        quotePrice.setQuoteVersion(quoteVersion!=null?quoteVersion: quoteOffer != null ? quoteOffer.getQuoteVersion() : null);
        quotePrice.setQuoteOffer(quoteOffer);
        
        Optional<QuotePrice> price = pricesPerType.get(key).stream().reduce((a, b) -> {
        	quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
		    quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
		    quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
		    quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
		    quotePrice.setAmountWithoutTaxWithoutDiscount(a.getAmountWithoutTaxWithoutDiscount().add(b.getAmountWithoutTaxWithoutDiscount()));
		    quotePrice.setTaxRate(a.getTaxRate());
		    quotePrice.setTax(a.getTax());
		    quotePrice.setChargeTemplate(a.getChargeTemplate());
		    if(a.getRecurrenceDuration()!=null) {
		    	quotePrice.setRecurrenceDuration(a.getRecurrenceDuration());
		    }
		    if(a.getRecurrencePeriodicity()!=null) {
		    	quotePrice.setRecurrencePeriodicity(a.getRecurrencePeriodicity());
		    }
			quotePrice.setCurrencyCode(!StringUtils.isBlank(a.getCurrencyCode())?a.getCurrencyCode():b.getCurrencyCode());
		    return quotePrice;
        });
        return price;
    }
}
