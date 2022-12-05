package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "exterBankTransDomain", "subFamilyCode" })
@XmlRootElement(name = "Fmly")
public class Family {     
    @XmlElement(name = "Cd", required = true)
    protected String exterBankTransDomain;
    @XmlElement(name = "SubFmlyCd", required = true)
    protected String subFamilyCode;

    public String getExterBankTransDomain() {
        return exterBankTransDomain;
    }
    public void setExterBankTransDomain(String exterBankTransDomain) {
        this.exterBankTransDomain = exterBankTransDomain;
    }
    public String getSubFamilyCode() {
        return subFamilyCode;
    }
    public void setSubFamilyCode(String subFamilyCode) {
        this.subFamilyCode = subFamilyCode;
    }
}