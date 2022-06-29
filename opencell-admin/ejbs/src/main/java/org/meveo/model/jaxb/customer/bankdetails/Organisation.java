package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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