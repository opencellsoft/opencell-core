package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.cpq.CurrencyDetailDto;
import org.meveo.api.dto.cpq.TaxDetailDTO;
import org.meveo.model.quote.QuoteVersion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteXMLHeader extends Header {
    @XmlElement
    private org.meveo.api.dto.cpq.xml.Customer customer;
    @XmlElement
    private org.meveo.api.dto.cpq.xml.Seller seller;
    @XmlElement
    private CurrencyDetailDto currency;
    @XmlElement
    private TaxDetailDTO taxDetail;

    public QuoteXMLHeader() {
    }

    public QuoteXMLHeader(BillingAccount billingAccount, Contract contract, QuoteVersion quoteVersion, String quoteCode, Date startDate,
                          Long duration, int opportunityDuration, String customerReference, String registrationNumber,
                          Date validFromDate, Date validToDate, String comment, org.meveo.api.dto.cpq.xml.Customer customer, org.meveo.api.dto.cpq.xml.Seller seller,
                          CurrencyDetailDto currency, TaxDetailDTO taxDetail) {
        super(billingAccount, contract, quoteVersion.getQuoteVersion(), quoteCode, startDate,
                duration, opportunityDuration, customerReference, registrationNumber,
                validFromDate, validToDate, comment, quoteVersion.getStartDate(), quoteVersion.getEndDate());
        this.customer = customer;
        this.seller = seller;
        this.currency = currency;
        this.taxDetail = taxDetail;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public org.meveo.api.dto.cpq.xml.Seller getSeller() {
        return seller;
    }

    public void setSeller(org.meveo.api.dto.cpq.xml.Seller seller) {
        this.seller = seller;
    }

    public CurrencyDetailDto getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDetailDto currency) {
        this.currency = currency;
    }
}