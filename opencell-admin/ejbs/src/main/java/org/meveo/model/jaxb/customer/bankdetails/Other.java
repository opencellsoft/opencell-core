package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id", "schemeName", "issuer" })
@XmlRootElement(name = "Othr")
public class Other { 
    @XmlElement(name = "Id", required = true)
    protected String id;
    @XmlElement(name = "SchmeNm")
    protected SchemeName schemeName;    
    @XmlElement(name = "Issr")
    protected String issuer;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public SchemeName getSchemeName() {
        return schemeName;
    }
    public void setSchemeName(SchemeName schemeName) {
        this.schemeName = schemeName;
    }
    public String getIssuer() {
        return issuer;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }    
    
    public Other() {
    }    
}