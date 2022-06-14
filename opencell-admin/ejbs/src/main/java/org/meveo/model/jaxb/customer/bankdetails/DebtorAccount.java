package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id" })
@XmlRootElement(name = "DbtrAcct")
public class DebtorAccount {
    @XmlElement(name = "Id", required = true)
    protected Identification id;   

    public DebtorAccount() {
    }

    public Identification getId() {
        return id;
    }
    public void setId(Identification id) {
        this.id = id;
    }
}