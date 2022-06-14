package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

    public CreditorAccount() {
    }
}