package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "identification" })
@XmlRootElement(name = "CdtrAcct")
public class CreditorAccount {
    @XmlElement(name = "Id", required = true)
    protected Identification identification;

    public Identification getIdentification() {
        return identification;
    }
    public void setIdentification(Identification identification) {
        this.identification = identification;
    }
}