package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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