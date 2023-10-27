package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteLine {
    @XmlAttribute
    private BigDecimal quantity;

    @XmlAttribute
    private String accountingArticleCode;
    @XmlAttribute
    private String accountingArticleLabel;

    @XmlElementWrapper(name = "prices")
    @XmlElement(name = "price")
    private List<PriceDTO> prices;
    
    private Offer offer;
    
    @XmlAttribute
    private String consumer;
    public QuoteLine(QuoteArticleLine line,Offer offer, Map<String, TradingCurrency> currencies) {
        this.quantity = line.getQuantity();
        this.accountingArticleCode = line.getAccountingArticle().getCode();
        this.accountingArticleLabel = line.getAccountingArticle().getDescription(); 
        if(line.getQuoteVersion()!=null) {
        	var quote=line.getQuoteVersion().getQuote();
        	if(line.getQuoteProduct()!=null
                    &&  line.getQuoteProduct().getQuoteOffer() != null
                    && line.getQuoteProduct().getQuoteOffer().getUserAccount() != null
            )
        		this.consumer= line.getQuoteProduct().getQuoteOffer().getUserAccount().getCode();


        }
        this.prices = aggregatePricesPerType(line.getQuotePrices(), currencies);
        this.offer= offer;
        
        
    }

    private List<PriceDTO> aggregatePricesPerType(List<QuotePrice> baPrices, Map<String, TradingCurrency> currencies) {
        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = baPrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        return pricesPerType
                .keySet()
                .stream()
                .map(key -> reducePrices(key, pricesPerType))
                .filter(Optional::isPresent)
                .map(price -> new PriceDTO(price.get(), currencies.get(price.get().getCurrencyCode())))
                .collect(Collectors.toList());
    }

    private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType) {
        return pricesPerType.get(key).stream().reduce((a, b) -> {
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(key);
            quotePrice.setPriceLevelEnum(PriceLevelEnum.OFFER);
            quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
            quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
            quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
            quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
            quotePrice.setTaxRate(a.getTaxRate());
            quotePrice.setTax(a.getTax());
            return quotePrice;
        });
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public List<PriceDTO> getPrices() {
        return prices;
    }

    public void setPrices(List<PriceDTO> prices) {
        this.prices = prices;
    }

	public String getAccountingArticleCode() {
		return accountingArticleCode;
	}

	public void setAccountingArticleCode(String accountingArticleCode) {
		this.accountingArticleCode = accountingArticleCode;
	}

	public String getAccountingArticleLabel() {
		return accountingArticleLabel;
	}

	public void setAccountingArticleLabel(String accountingArticleLabel) {
		this.accountingArticleLabel = accountingArticleLabel;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
	
	
    
    
}
