package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Quote {

    @XmlElementWrapper(name = "billableAccounts")
    @XmlElement(name = "billableAccount")
    private List<BillableAccount> billableAccounts;
    private String quoteNumber;
    private Date quoteDate;

    public Quote(List<BillableAccount> billableAccounts, String quoteNumber, Date quoteDate) {
        this.billableAccounts = billableAccounts;
        this.quoteNumber = quoteNumber;
        this.quoteDate = quoteDate;
    }

    public List<BillableAccount> getBillableAccounts() {
        return billableAccounts;
    }

    public void setBillableAccounts(List<BillableAccount> billableAccounts) {
        this.billableAccounts = billableAccounts;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }
}
