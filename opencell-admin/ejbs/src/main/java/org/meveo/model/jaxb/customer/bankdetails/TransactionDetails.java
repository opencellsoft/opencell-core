package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "bankTransactionCode", "references", "relatedParties", "relatedAgents", "remittanceInformation" })
@XmlRootElement(name = "TxDtls")
public class TransactionDetails {
    @XmlElement(name = "BkTxCd")
    protected BankTransactionCode bankTransactionCode;
    @XmlElement(name = "Refs")
    protected References references;
    @XmlElement(name = "RltdPties")
    protected RelatedParties relatedParties;    
    @XmlElement(name = "RltdAgts")
    protected RelatedAgents relatedAgents;    
    @XmlElement(name = "RmtInf")
    protected RemittanceInformation remittanceInformation;

    public BankTransactionCode getBankTransactionCode() {
        return bankTransactionCode;
    }
    public void setBankTransactionCode(BankTransactionCode bankTransactionCode) {
        this.bankTransactionCode = bankTransactionCode;
    }
    public References getReferences() {
        return references;
    }
    public void setReferences(References references) {
        this.references = references;
    }
    public RelatedParties getRelatedParties() {
        return relatedParties;
    }
    public void setRelatedParties(RelatedParties relatedParties) {
        this.relatedParties = relatedParties;
    }
    public RelatedAgents getRelatedAgents() {
        return relatedAgents;
    }
    public void setRelatedAgents(RelatedAgents relatedAgents) {
        this.relatedAgents = relatedAgents;
    }
    public RemittanceInformation getRemittanceInformation() {
        return remittanceInformation;
    }
    public void setRemittanceInformation(RemittanceInformation remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }  
}