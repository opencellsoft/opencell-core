package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "creditorRefInf" })
@XmlRootElement(name = "Strd")
public class Structured {
    @XmlElement(name = "CdtrRefInf")
    protected CreditorRefInf creditorRefInf;

    public CreditorRefInf getCreditorRefInf() {
        return creditorRefInf;
    }
    public void setCreditorRefInf(CreditorRefInf creditorRefInf) {
        this.creditorRefInf = creditorRefInf;
    }   
}