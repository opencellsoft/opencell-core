package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "quote")
public class QuoteXmlDto {

    @XmlElement
    private QuoteXMLHeader header;
    @XmlElement
    private Details details;

    public QuoteXmlDto() {
    }

    public QuoteXmlDto(QuoteXMLHeader header, Details details) {
        this.header = header;
        this.details = details;
    }

    public QuoteXMLHeader getHeader() {
        return header;
    }

    public Details getDetails() {
        return details;
    }
}
