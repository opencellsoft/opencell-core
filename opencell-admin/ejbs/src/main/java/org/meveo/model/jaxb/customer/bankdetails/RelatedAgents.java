package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "debtorAgent", "creditorAgent" })
@XmlRootElement(name = "RltdAgts")
public class RelatedAgents {
    @XmlElement(name = "DbtrAgt")
    protected DebtorAgent debtorAgent;
    @XmlElement(name = "CdtrAgt")
    protected CreditorAgent creditorAgent;    

    public DebtorAgent getDebtorAgent() {
        return debtorAgent;
    }
    public void setDebtorAgent(DebtorAgent debtorAgent) {
        this.debtorAgent = debtorAgent;
    }
    public CreditorAgent getCreditorAgent() {
        return creditorAgent;
    }
    public void setCreditorAgent(CreditorAgent creditorAgent) {
        this.creditorAgent = creditorAgent;
    }
}