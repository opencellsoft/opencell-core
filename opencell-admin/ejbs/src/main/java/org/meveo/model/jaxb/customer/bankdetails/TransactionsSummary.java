package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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