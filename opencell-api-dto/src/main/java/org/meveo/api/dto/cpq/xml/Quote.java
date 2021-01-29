package org.meveo.api.dto.cpq.xml;

import java.util.Date;
import java.util.List;

public class Quote {

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
