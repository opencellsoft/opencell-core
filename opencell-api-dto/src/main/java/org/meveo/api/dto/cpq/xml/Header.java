package org.meveo.api.dto.cpq.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class Header {
    @XmlElement
    private BillingAccount billingAccount;
    @XmlElement
    private Contract contract;
    @XmlElement
    private int quoteVersion;
    @XmlElement
    private String quoteCode;
    @XmlElement
    private Date startDate;
    @XmlElement
    private int duration;
    @XmlElement
    private int opportunityDuration;
    @XmlElement
    private String customerReference;
    @XmlElement
    private String registrationNumber;
    @XmlElement
    private Date validFromDate;
    @XmlElement
    private Date validToDate;
    
    
    

    public Header() {
    }

    public Header(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * @param contract the contract to set
	 */
	public void setContract(Contract contract) {
		this.contract = contract;
	}

	/**
	 * @return the quoteVersion
	 */
	public int getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(int quoteVersion) {
		this.quoteVersion = quoteVersion;
	}

	/**
	 * @return the quoteCode
	 */
	public String getQuoteCode() {
		return quoteCode;
	}

	/**
	 * @param quoteCode the quoteCode to set
	 */
	public void setQuoteCode(String quoteCode) {
		this.quoteCode = quoteCode;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the opportunityDuration
	 */
	public int getOpportunityDuration() {
		return opportunityDuration;
	}

	/**
	 * @param opportunityDuration the opportunityDuration to set
	 */
	public void setOpportunityDuration(int opportunityDuration) {
		this.opportunityDuration = opportunityDuration;
	}

	/**
	 * @return the customerReference
	 */
	public String getCustomerReference() {
		return customerReference;
	}

	/**
	 * @param customerReference the customerReference to set
	 */
	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	/**
	 * @return the registrationNumber
	 */
	public String getRegistrationNumber() {
		return registrationNumber;
	}

	/**
	 * @param registrationNumber the registrationNumber to set
	 */
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	/**
	 * @return the validFromDate
	 */
	public Date getValidFromDate() {
		return validFromDate;
	}

	/**
	 * @param validFromDate the validFromDate to set
	 */
	public void setValidFromDate(Date validFromDate) {
		this.validFromDate = validFromDate;
	}

	/**
	 * @return the validToDate
	 */
	public Date getValidToDate() {
		return validToDate;
	}

	/**
	 * @param validToDate the validToDate to set
	 */
	public void setValidToDate(Date validToDate) {
		this.validToDate = validToDate;
	}

	/**
	 * @param billingAccount the billingAccount to set
	 */
	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}
}
