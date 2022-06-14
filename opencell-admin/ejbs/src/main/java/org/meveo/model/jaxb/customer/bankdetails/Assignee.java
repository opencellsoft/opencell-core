package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "party" })
@XmlRootElement(name = "Assgne")
public class Assignee {
    @XmlElement(name = "Pty", required = true)
    protected Party party;

    public Assignee() {
    }

    public Party getParty() {
        return party;
    }
    public void setParty(Party party) {
        this.party = party;
    }
}