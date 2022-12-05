package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id" })
@XmlRootElement(name = "DbtrAcct")
public class DebtorAccount {
    @XmlElement(name = "Id", required = true)
    protected Identification id;   

    public Identification getId() {
        return id;
    }
    public void setId(Identification id) {
        this.id = id;
    }
}