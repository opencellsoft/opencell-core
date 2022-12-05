package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "listTEntriesrBankTransaction" })
@XmlRootElement(name = "TxsSummry")
public class TransactionsSummary {
    @XmlElement(name = "TtlNtriesPerBkTxCd")
    protected List<EntriesrBankTransaction> listTEntriesrBankTransaction;    

    public List<EntriesrBankTransaction> getListTEntriesrBankTransaction() {
        return listTEntriesrBankTransaction;
    }
    public void setListTEntriesrBankTransaction(List<EntriesrBankTransaction> listTEntriesrBankTransaction) {
        this.listTEntriesrBankTransaction = listTEntriesrBankTransaction;
    }  
}