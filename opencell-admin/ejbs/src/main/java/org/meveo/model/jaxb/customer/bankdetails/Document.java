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

    public MsgBankEmetteur getMessageBanqueEmetteur() {
        return messageBanqueEmetteur;
    }

    public void setMessageBanqueEmetteur(MsgBankEmetteur messageBanqueEmetteur) {
        this.messageBanqueEmetteur = messageBanqueEmetteur;
    }
}