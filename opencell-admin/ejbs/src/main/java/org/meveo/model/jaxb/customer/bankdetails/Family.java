package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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