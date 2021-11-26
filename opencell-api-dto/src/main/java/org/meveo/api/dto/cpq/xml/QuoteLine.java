package org.meveo.api.dto.cpq.xml;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;

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

    public QuoteLine(QuoteArticleLine line,Offer offer) {
        this.quantity = line.getQuantity();
        this.accountingArticleCode = line.getAccountingArticle().getCode();
        this.accountingArticleLabel = line.getAccountingArticle().getDescription();
        this.consumer=line.getQuoteVersion()!=null?(line.getQuoteVersion().getQuote().getUserAccount()!=null?line.getQuoteVersion().getQuote().getUserAccount().getCode():"N/A"):"N/A";
        this.prices = aggregatePricesPerType(line.getQuotePrices());
        this.offer= offer;
        
        
    }
    
    private List<PriceDTO> aggregatePricesPerType(List<QuotePrice> baPrices) {
        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = baPrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        return pricesPerType
                .keySet()
                .stream()
                .map(key -> reducePrices(key, pricesPerType))
                .filter(Optional::isPresent)
                .map(price -> new PriceDTO(price.get()))
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
