package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.cpq.PriceDTO;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder={"quote","quotePrices"})
public class Details {
    private Quote quote;
    @XmlElementWrapper(name = "quotePrices")
    @XmlElement(name = "price")
    List<PriceDTO> quotePrices;

    public Details(Quote quote, List<PriceDTO> quotePrices) {
        this.quote = quote;
        this.quotePrices = quotePrices;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public List<PriceDTO> getQuotePrices() {
        Collections.sort(quotePrices, Comparator.comparing(PriceDTO::getPriceType).reversed());
        return quotePrices;
    }
}
