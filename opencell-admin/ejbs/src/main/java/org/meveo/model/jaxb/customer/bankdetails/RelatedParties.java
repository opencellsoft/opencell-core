package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "debtor", "debtorAccount", "ultimateDebtor", "creditor", 
        "creditorAccount", "ultimateCreditor" })
@XmlRootElement(name = "RltdPties")
public class RelatedParties {
    @XmlElement(name = "Dbtr")
    protected Debtor debtor;
    @XmlElement(name = "DbtrAcct")
    protected DebtorAccount debtorAccount;
    @XmlElement(name = "UltmtDbtr")
    protected UltimateDebtor ultimateDebtor;    
    @XmlElement(name = "Cdtr")
    protected Creditor creditor;    
    @XmlElement(name = "CdtrAcct")
    protected CreditorAccount creditorAccount;
    @XmlElement(name = "UltmtCdtr")
    protected UltimateCreditor ultimateCreditor;
    
    public UltimateDebtor getUltimateDebtor() {
        return ultimateDebtor;
    }
    public void setUltimateDebtor(UltimateDebtor ultimateDebtor) {
        this.ultimateDebtor = ultimateDebtor;
    }
    public Creditor getCreditor() {
        return creditor;
    }
    public void setCreditor(Creditor creditor) {
        this.creditor = creditor;
    }
    public CreditorAccount getCreditorAccount() {
        return creditorAccount;
    }
    public void setCreditorAccount(CreditorAccount creditorAccount) {
        this.creditorAccount = creditorAccount;
    }
    public UltimateCreditor getUltimateCreditor() {
        return ultimateCreditor;
    }
    public void setUltimateCreditor(UltimateCreditor ultimateCreditor) {
        this.ultimateCreditor = ultimateCreditor;
    }
    public Debtor getDebtor() {
        return debtor;
    }
    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }
    public DebtorAccount getDebtorAccount() {
        return debtorAccount;
    }
    public void setDebtorAccount(DebtorAccount debtorAccount) {
        this.debtorAccount = debtorAccount;
    }  
}