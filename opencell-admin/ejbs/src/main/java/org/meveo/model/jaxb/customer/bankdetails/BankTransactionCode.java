package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
    
    public BankTransactionCode() {
    }
}