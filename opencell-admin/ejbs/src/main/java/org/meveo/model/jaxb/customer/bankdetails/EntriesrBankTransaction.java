package org.meveo.model.jaxb.customer.bankdetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "numbOfEntries", "bankTransactionCode" })
@XmlRootElement(name = "TtlNtriesPerBkTxCd")
public class EntriesrBankTransaction {
    @XmlElement(name = "NbOfNtries")
    protected Long numbOfEntries;        
    @XmlElement(name = "BkTxCd")
    protected BankTransactionCode bankTransactionCode;
    
    public Long getNumbOfEntries() {
        return numbOfEntries;
    }
    public void setNumbOfEntries(Long numbOfEntries) {
        this.numbOfEntries = numbOfEntries;
    }
    public BankTransactionCode getBankTransactionCode() {
        return bankTransactionCode;
    }
    public void setBankTransactionCode(BankTransactionCode bankTransactionCode) {
        this.bankTransactionCode = bankTransactionCode;
    }
}