package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id", "accountSwitchingRef", "orgPartyAndAccount", "updatedPartyAndAccount", "addtlInf", "transactionReport" })
@XmlRootElement(name = "Mod")
public class Modification {
    @XmlElement(name = "Id", required = true)
    protected String id;    
    @XmlElement(name = "AcctSwtchngRef", required = true)
    protected AccountSwitchingRef accountSwitchingRef;
    @XmlElement(name = "OrgnlPtyAndAcctId")
    protected OrgPartyAndAccount orgPartyAndAccount;    
    @XmlElement(name = "UpdtdPtyAndAcctId", required = true)
    protected UpdatedPartyAndAccount updatedPartyAndAccount;    
    @XmlElement(name = "AddtlInf")
    protected String addtlInf;    
    @XmlElement(name = "TxRprt")
    protected List<TransactionReport> transactionReport;
    
    public String getId() {
        return id;
    }    
    public void setId(String id) {
        this.id = id;
    }
    public AccountSwitchingRef getAccountSwitchingRef() {
        return accountSwitchingRef;
    }
    public void setAccountSwitchingRef(AccountSwitchingRef accountSwitchingRef) {
        this.accountSwitchingRef = accountSwitchingRef;
    }
    public OrgPartyAndAccount getOrgPartyAndAccount() {
        return orgPartyAndAccount;
    }
    public void setOrgPartyAndAccount(OrgPartyAndAccount orgPartyAndAccount) {
        this.orgPartyAndAccount = orgPartyAndAccount;
    }
    public UpdatedPartyAndAccount getUpdatedPartyAndAccount() {
        return updatedPartyAndAccount;
    }
    public void setUpdatedPartyAndAccount(UpdatedPartyAndAccount updatedPartyAndAccount) {
        this.updatedPartyAndAccount = updatedPartyAndAccount;
    }
    public String getAddtlInf() {
        return addtlInf;
    }
    public void setAddtlInf(String addtlInf) {
        this.addtlInf = addtlInf;
    }
    public List<TransactionReport> getTransactionReport() {
        return transactionReport;
    }
    public void setTransactionReport(List<TransactionReport> transactionReport) {
        this.transactionReport = transactionReport;
    }
}