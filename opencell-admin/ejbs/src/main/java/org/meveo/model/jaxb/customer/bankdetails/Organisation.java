package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "anyBIC", "other" })
@XmlRootElement(name = "OrgId")
public class Organisation {
    @XmlElement(name = "AnyBIC")
    protected String anyBIC;
    @XmlElement(name = "Othr")
    protected List<Other> other;    

    public String getAnyBIC() {
        return anyBIC;
    }
    public void setAnyBIC(String anyBIC) {
        this.anyBIC = anyBIC;
    }
    public List<Other> getOther() {
        return other;
    }
    public void setOther(List<Other> other) {
        this.other = other;
    }   
}