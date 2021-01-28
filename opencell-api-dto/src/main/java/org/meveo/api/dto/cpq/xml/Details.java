package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.cpq.PriceDTO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

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
}
