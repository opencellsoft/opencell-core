package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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