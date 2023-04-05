package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteXMLHeader extends Header {
    @XmlElement
    private org.meveo.api.dto.cpq.xml.CustomerAccount customerAccount;
    @XmlElement
    private org.meveo.api.dto.cpq.xml.Seller seller;

    public QuoteXMLHeader() {
    }

    public QuoteXMLHeader(BillingAccount billingAccount, Contract contract, int quoteVersion, String quoteCode, Date startDate,
                          Long duration, int opportunityDuration, String customerReference, String registrationNumber,
                          Date validFromDate, Date validToDate, String comment, org.meveo.api.dto.cpq.xml.CustomerAccount customerAccount, org.meveo.api.dto.cpq.xml.Seller seller) {
        super(billingAccount, contract, quoteVersion, quoteCode, startDate,
                duration, opportunityDuration, customerReference, registrationNumber,
                validFromDate, validToDate, comment);
        this.customerAccount = customerAccount;
        this.seller = seller;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public org.meveo.api.dto.cpq.xml.Seller getSeller() {
        return seller;
    }

    public void setSeller(org.meveo.api.dto.cpq.xml.Seller seller) {
        this.seller = seller;
    }

}
