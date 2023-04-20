package org.meveo.api.dto.cpq.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
  
@XmlAccessorType(XmlAccessType.FIELD)
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
    private Long duration;
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
    @XmlElement
    private String comment;
	@XmlElement
	private Date quoteVersionStartDate;
	@XmlElement
	private Date quoteVersionEndDate;

    public Header() {
    }

    public Header(BillingAccount billingAccount, Contract contract, int quoteVersion, String quoteCode, Date startDate,
			Long duration, int opportunityDuration, String customerReference, String registrationNumber,
			Date validFromDate, Date validToDate,String comment, Date quoteVersionStartDate, Date quoteVersionEndDate) {
		super();
		this.billingAccount = billingAccount;
		this.contract = contract;
		this.quoteVersion = quoteVersion;
		this.quoteCode = quoteCode;
		this.startDate = startDate;
		this.duration = duration;
		this.opportunityDuration = opportunityDuration;
		this.customerReference = customerReference;
		this.registrationNumber = registrationNumber;
		this.validFromDate = validFromDate;
		this.validToDate = validToDate;
		this.comment=comment;
		this.quoteVersionStartDate = quoteVersionStartDate;
		this.quoteVersionEndDate = quoteVersionEndDate;
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

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getQuoteVersionStartDate() {
		return quoteVersionStartDate;
	}

	public void setQuoteVersionStartDate(Date quoteVersionStartDate) {
		this.quoteVersionStartDate = quoteVersionStartDate;
	}

	public Date getQuoteVersionEndDate() {
		return quoteVersionEndDate;
	}

	public void setQuoteVersionEndDate(Date quoteVersionEndDate) {
		this.quoteVersionEndDate = quoteVersionEndDate;
	}
}
