package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "domain" })
@XmlRootElement(name = "BkTxCd")
public class BankTransactionCode {    
    @XmlElement(name = "Domn")
    protected Domain domain;
    
    public Domain getDomain() {
        return domain;
    }    
    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}