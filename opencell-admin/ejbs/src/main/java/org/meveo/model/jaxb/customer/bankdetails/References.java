package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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