package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "agt" })
@XmlRootElement(name = "Cretr")
public class Creator {
    @XmlElement(name = "Agt", required = true)
    protected Agent agt;

    public Agent getAgt() {
        return agt;
    }
    public void setAgt(Agent agt) {
        this.agt = agt;
    }
}