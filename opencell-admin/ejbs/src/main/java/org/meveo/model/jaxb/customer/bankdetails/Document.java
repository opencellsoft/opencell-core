package org.meveo.model.jaxb.customer.bankdetails;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "messageBanqueEmetteur" })
@XmlRootElement(name="Document", namespace="urn:iso:std:iso:20022:tech:xsd:acmt.02z.001.01:Report")
public class Document {
    @XmlElement(name = "AcctSwtchngInfSvcRptV01", required = true)
    protected MsgBankEmetteur messageBanqueEmetteur;
    
    private static final Logger log = LoggerFactory.getLogger(Document.class);

    public Document() {
    }

    public MsgBankEmetteur getMessageBanqueEmetteur() {
        return messageBanqueEmetteur;
    }

    public void setMessageBanqueEmetteur(MsgBankEmetteur messageBanqueEmetteur) {
        this.messageBanqueEmetteur = messageBanqueEmetteur;
    }

    public static void main(String[] args) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Document.class);

            jaxbContext.generateSchema(new SchemaOutputResolver() {
                @Override
                public Result createOutput(String namespaceUri,
                        String suggestedFileName) throws IOException {
                    File file = new File("/tmp/import_customer.xsd");
                    StreamResult result = new StreamResult(file);
                    result.setSystemId(file.toURI().toURL().toString());
                    return result;
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("error = {}", e);
        }
    }
}