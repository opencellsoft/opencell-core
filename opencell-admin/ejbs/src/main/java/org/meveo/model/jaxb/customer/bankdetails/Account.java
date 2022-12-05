package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "iBAN" })
@XmlRootElement(name = "Acct")
public class Account {
    @XmlElement(name = "IBAN", required = true)
    protected String iBAN;
    
    public String getiBAN() {
        return iBAN;
    }
    public void setiBAN(String iBAN) {
        this.iBAN = iBAN;
    }
}