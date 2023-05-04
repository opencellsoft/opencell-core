package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "party", "name", "account", "agent" })
@XmlRootElement(name = "UpdtdPtyAndAcctId")
public class UpdatedPartyAndAccount {    
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setParty(Party party) {
        this.party = party;
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