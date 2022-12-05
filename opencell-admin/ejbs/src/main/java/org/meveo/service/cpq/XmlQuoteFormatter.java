package org.meveo.service.cpq;

import org.meveo.api.dto.cpq.xml.QuoteXmlDto;

import jakarta.ejb.Stateless;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

@Stateless
public class XmlQuoteFormatter {
    public String format(QuoteXmlDto quoteXmlDto) throws JAXBException {

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(QuoteXmlDto.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(quoteXmlDto, sw);
            return sw.toString();
        } catch (JAXBException e) {
           throw e;
        }
    }
}
