package org.meveo.model.jaxb.customer.bankdetails;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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