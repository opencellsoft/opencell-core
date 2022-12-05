package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "transactionsSummary", "transactionDetails" })
@XmlRootElement(name = "TxRprt")
public class TransactionReport {
    @XmlElement(name = "TxsSummry")
    protected TransactionsSummary transactionsSummary;
    @XmlElement(name = "TxDtls")
    protected List<TransactionDetails> transactionDetails;
    
    public TransactionsSummary getTransactionsSummary() {
        return transactionsSummary;
    }
    public void setTransactionsSummary(TransactionsSummary transactionsSummary) {
        this.transactionsSummary = transactionsSummary;
    }
    public List<TransactionDetails> getTransactionDetails() {
        return transactionDetails;
    }
    public void setTransactionDetails(List<TransactionDetails> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }
}