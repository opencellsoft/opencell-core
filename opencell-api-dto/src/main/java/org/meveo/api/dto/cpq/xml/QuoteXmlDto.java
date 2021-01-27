package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "quote")
public class QuoteXmlDto {

    @XmlElement
    private Header header;

    public QuoteXmlDto() {
    }

    public QuoteXmlDto(Header header) {
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }
}
