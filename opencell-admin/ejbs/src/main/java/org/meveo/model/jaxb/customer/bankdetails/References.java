package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "endToEndId", "mandateId" })
@XmlRootElement(name = "Refs")
public class References {
    @XmlElement(name = "EndToEndId")
    protected String endToEndId;
    @XmlElement(name = "MndtId")
    protected String mandateId;

    public String getEndToEndId() {
        return endToEndId;
    }
    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }
    public String getMandateId() {
        return mandateId;
    }
    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }  
}