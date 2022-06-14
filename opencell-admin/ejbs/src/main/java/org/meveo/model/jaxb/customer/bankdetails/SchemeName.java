package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "code", "proprietary" })
@XmlRootElement(name = "SchmeNm")
public class SchemeName { 
    @XmlElement(name = "Cd")//, required = true
    protected String code;
    @XmlElement(name = "Prtry", required = true)
    protected String proprietary;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getProprietary() {
        return proprietary;
    }
    public void setProprietary(String proprietary) {
        this.proprietary = proprietary;
    }    
    
    public SchemeName() {
    } 
}