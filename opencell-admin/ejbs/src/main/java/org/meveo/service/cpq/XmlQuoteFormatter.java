package org.meveo.service.cpq;

import org.meveo.api.dto.cpq.xml.QuoteXmlDto;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
