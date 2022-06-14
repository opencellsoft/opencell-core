package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "agt" })
@XmlRootElement(name = "Cretr")
public class Creator {
    @XmlElement(name = "Agt", required = true)
    protected Agent agt;

    public Creator() {
    }

    public Agent getAgt() {
        return agt;
    }
    public void setAgt(Agent agt) {
        this.agt = agt;
    }
}