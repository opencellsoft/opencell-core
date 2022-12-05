package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "exterBankTransDomain", "family" })
@XmlRootElement(name = "Domn")
public class Domain {     
    @XmlElement(name = "Cd", required = true)
    protected String exterBankTransDomain;
    @XmlElement(name = "Fmly", required = true)
    protected Family family;   

    public String getExterBankTransDomain() {
        return exterBankTransDomain;
    }
    public void setExterBankTransDomain(String exterBankTransDomain) {
        this.exterBankTransDomain = exterBankTransDomain;
    }
    public Family getFamily() {
        return family;
    }
    public void setFamily(Family family) {
        this.family = family;
    }
}