package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "party", "name", "account", "agent" })
@XmlRootElement(name = "OrgnlPtyAndAcctId")
public class OrgPartyAndAccount {
    @XmlElement(name = "Pty", required = true)
    protected Party party;
    @XmlElement(name = "Nm")
    protected String name;
    @XmlElement(name = "Acct")
    protected Account account;    
    @XmlElement(name = "Agt")
    protected Agent agent;
    
    public Party getParty() {
        return party;
    }
    public void setParty(Party party) {
        this.party = party;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public Agent getAgent() {
        return agent;
    }
    public void setAgent(Agent agent) {
        this.agent = agent;
    }  
}