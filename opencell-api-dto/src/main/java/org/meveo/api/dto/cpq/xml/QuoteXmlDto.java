package org.meveo.api.dto.cpq.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "quote")
public class QuoteXmlDto {

    @XmlElement
    private Header header;
    @XmlElement
    private Details details;

    public QuoteXmlDto() {
    }

    public QuoteXmlDto(Header header, Details details) {
        this.header = header;
        this.details = details;
    }

    public Header getHeader() {
        return header;
    }

    public Details getDetails() {
        return details;
    }
}
