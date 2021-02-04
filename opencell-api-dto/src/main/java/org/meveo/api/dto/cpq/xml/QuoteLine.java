package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.model.quote.QuoteArticleLine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.math.BigDecimal;
import java.util.List;
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

    public QuoteLine(QuoteArticleLine line, String code, String label) {
        this.quantity = line.getQuantity();
        this.accountingArticleCode = code;
        this.accountingArticleLabel = label;
        this.prices = line.getQuotePrices().stream()
                .map(PriceDTO::new)
                .collect(Collectors.toList());;
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
}
