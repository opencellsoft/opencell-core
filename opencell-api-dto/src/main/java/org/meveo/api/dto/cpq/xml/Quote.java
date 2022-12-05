package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.CustomFieldsDto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Quote {

    @XmlElementWrapper(name = "billableAccounts")
    @XmlElement(name = "billableAccount")
    private List<BillableAccount> billableAccounts;
    private String quoteNumber;
    private Date quoteDate;
    private CustomFieldsDto customFields;
    private String defaultConsumer;
    

    public Quote(List<BillableAccount> billableAccounts, String quoteNumber, Date quoteDate, CustomFieldsDto customFields) {
        this.billableAccounts = billableAccounts;
        this.quoteNumber = quoteNumber;
        this.quoteDate = quoteDate;
        this.customFields=customFields;
    }

    public Quote(List<BillableAccount> billableAccounts, String quoteNumber, Date quoteDate, CustomFieldsDto customFields, String defaultCustomer) {
        this.billableAccounts = billableAccounts;
        this.quoteNumber = quoteNumber;
        this.quoteDate = quoteDate;
        this.customFields = customFields;
        this.defaultConsumer = defaultCustomer;
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

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

    public String getDefaultConsumer() {
        return defaultConsumer;
    }

    public void setDefaultConsumer(String defaultConsumer) {
        this.defaultConsumer = defaultConsumer;
    }
}
