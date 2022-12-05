package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "party" })
@XmlRootElement(name = "Assgne")
public class Assignee {
    @XmlElement(name = "Pty", required = true)
    protected Party party;

    public Party getParty() {
        return party;
    }
    public void setParty(Party party) {
        this.party = party;
    }
}