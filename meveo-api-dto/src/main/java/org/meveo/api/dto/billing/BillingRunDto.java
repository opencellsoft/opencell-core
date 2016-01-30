package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;

@XmlRootElement(name = "BillingRun")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingRunDto  extends BaseDto{
	
		private static final long serialVersionUID = 1L;

		private Date processDate;
		private BillingRunStatusEnum status;
		private Date statusDate;
		private BillingCycleDto billingCycle;
		private Integer billingAccountNumber;
		private Integer billableBillingAcountNumber;
		private Integer producibleInvoiceNumber;
		private BigDecimal producibleAmountWithoutTax;
		private BigDecimal producibleAmountTax;
		private Integer InvoiceNumber;
		private BigDecimal producibleAmountWithTax;
		private BigDecimal prAmountWithoutTax;
		private BigDecimal prAmountWithTax;
		private BigDecimal prAmountTax;
		private BillingRunTypeEnum processType;
		private Date startDate;
		private Date endDate;
	    private Date invoiceDate;
	    private Date lastTransactionDate;
		private String rejectionReason;
		private String currencyCode;
		private String countryCode;
		private String languageCode;
		private String selectedBillingAccounts;
		private Boolean xmlInvoiceGenerated = Boolean.FALSE;
		
		public BillingRunDto(){
			
		}

		/**
		 * @return the processDate
		 */
		public Date getProcessDate() {
			return processDate;
		}

		/**
		 * @param processDate the processDate to set
		 */
		public void setProcessDate(Date processDate) {
			this.processDate = processDate;
		}

		/**
		 * @return the status
		 */
		public BillingRunStatusEnum getStatus() {
			return status;
		}

		/**
		 * @param status the status to set
		 */
		public void setStatus(BillingRunStatusEnum status) {
			this.status = status;
		}

		/**
		 * @return the statusDate
		 */
		public Date getStatusDate() {
			return statusDate;
		}

		/**
		 * @param statusDate the statusDate to set
		 */
		public void setStatusDate(Date statusDate) {
			this.statusDate = statusDate;
		}

		/**
		 * @return the billingCycle
		 */
		public BillingCycleDto getBillingCycle() {
			return billingCycle;
		}

		/**
		 * @param billingCycle the billingCycle to set
		 */
		public void setBillingCycle(BillingCycleDto billingCycle) {
			this.billingCycle = billingCycle;
		}

		/**
		 * @return the billingAccountNumber
		 */
		public Integer getBillingAccountNumber() {
			return billingAccountNumber;
		}

		/**
		 * @param billingAccountNumber the billingAccountNumber to set
		 */
		public void setBillingAccountNumber(Integer billingAccountNumber) {
			this.billingAccountNumber = billingAccountNumber;
		}

		/**
		 * @return the billableBillingAcountNumber
		 */
		public Integer getBillableBillingAcountNumber() {
			return billableBillingAcountNumber;
		}

		/**
		 * @param billableBillingAcountNumber the billableBillingAcountNumber to set
		 */
		public void setBillableBillingAcountNumber(Integer billableBillingAcountNumber) {
			this.billableBillingAcountNumber = billableBillingAcountNumber;
		}

		/**
		 * @return the producibleInvoiceNumber
		 */
		public Integer getProducibleInvoiceNumber() {
			return producibleInvoiceNumber;
		}

		/**
		 * @param producibleInvoiceNumber the producibleInvoiceNumber to set
		 */
		public void setProducibleInvoiceNumber(Integer producibleInvoiceNumber) {
			this.producibleInvoiceNumber = producibleInvoiceNumber;
		}

		/**
		 * @return the producibleAmountWithoutTax
		 */
		public BigDecimal getProducibleAmountWithoutTax() {
			return producibleAmountWithoutTax;
		}

		/**
		 * @param producibleAmountWithoutTax the producibleAmountWithoutTax to set
		 */
		public void setProducibleAmountWithoutTax(BigDecimal producibleAmountWithoutTax) {
			this.producibleAmountWithoutTax = producibleAmountWithoutTax;
		}

		/**
		 * @return the producibleAmountTax
		 */
		public BigDecimal getProducibleAmountTax() {
			return producibleAmountTax;
		}

		/**
		 * @param producibleAmountTax the producibleAmountTax to set
		 */
		public void setProducibleAmountTax(BigDecimal producibleAmountTax) {
			this.producibleAmountTax = producibleAmountTax;
		}

		/**
		 * @return the invoiceNumber
		 */
		public Integer getInvoiceNumber() {
			return InvoiceNumber;
		}

		/**
		 * @param invoiceNumber the invoiceNumber to set
		 */
		public void setInvoiceNumber(Integer invoiceNumber) {
			InvoiceNumber = invoiceNumber;
		}

		/**
		 * @return the producibleAmountWithTax
		 */
		public BigDecimal getProducibleAmountWithTax() {
			return producibleAmountWithTax;
		}

		/**
		 * @param producibleAmountWithTax the producibleAmountWithTax to set
		 */
		public void setProducibleAmountWithTax(BigDecimal producibleAmountWithTax) {
			this.producibleAmountWithTax = producibleAmountWithTax;
		}

		/**
		 * @return the prAmountWithoutTax
		 */
		public BigDecimal getPrAmountWithoutTax() {
			return prAmountWithoutTax;
		}

		/**
		 * @param prAmountWithoutTax the prAmountWithoutTax to set
		 */
		public void setPrAmountWithoutTax(BigDecimal prAmountWithoutTax) {
			this.prAmountWithoutTax = prAmountWithoutTax;
		}

		/**
		 * @return the prAmountWithTax
		 */
		public BigDecimal getPrAmountWithTax() {
			return prAmountWithTax;
		}

		/**
		 * @param prAmountWithTax the prAmountWithTax to set
		 */
		public void setPrAmountWithTax(BigDecimal prAmountWithTax) {
			this.prAmountWithTax = prAmountWithTax;
		}

		/**
		 * @return the prAmountTax
		 */
		public BigDecimal getPrAmountTax() {
			return prAmountTax;
		}

		/**
		 * @param prAmountTax the prAmountTax to set
		 */
		public void setPrAmountTax(BigDecimal prAmountTax) {
			this.prAmountTax = prAmountTax;
		}

		/**
		 * @return the processType
		 */
		public BillingRunTypeEnum getProcessType() {
			return processType;
		}

		/**
		 * @param processType the processType to set
		 */
		public void setProcessType(BillingRunTypeEnum processType) {
			this.processType = processType;
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
		 * @return the endDate
		 */
		public Date getEndDate() {
			return endDate;
		}

		/**
		 * @param endDate the endDate to set
		 */
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		/**
		 * @return the invoiceDate
		 */
		public Date getInvoiceDate() {
			return invoiceDate;
		}

		/**
		 * @param invoiceDate the invoiceDate to set
		 */
		public void setInvoiceDate(Date invoiceDate) {
			this.invoiceDate = invoiceDate;
		}

		/**
		 * @return the lastTransactionDate
		 */
		public Date getLastTransactionDate() {
			return lastTransactionDate;
		}

		/**
		 * @param lastTransactionDate the lastTransactionDate to set
		 */
		public void setLastTransactionDate(Date lastTransactionDate) {
			this.lastTransactionDate = lastTransactionDate;
		}

		/**
		 * @return the rejectionReason
		 */
		public String getRejectionReason() {
			return rejectionReason;
		}

		/**
		 * @param rejectionReason the rejectionReason to set
		 */
		public void setRejectionReason(String rejectionReason) {
			this.rejectionReason = rejectionReason;
		}

		/**
		 * @return the currencyCode
		 */
		public String getCurrencyCode() {
			return currencyCode;
		}

		/**
		 * @param currencyCode the currencyCode to set
		 */
		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

		/**
		 * @return the countryCode
		 */
		public String getCountryCode() {
			return countryCode;
		}

		/**
		 * @param countryCode the countryCode to set
		 */
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		/**
		 * @return the languageCode
		 */
		public String getLanguageCode() {
			return languageCode;
		}

		/**
		 * @param languageCode the languageCode to set
		 */
		public void setLanguageCode(String languageCode) {
			this.languageCode = languageCode;
		}

		/**
		 * @return the selectedBillingAccounts
		 */
		public String getSelectedBillingAccounts() {
			return selectedBillingAccounts;
		}

		/**
		 * @param selectedBillingAccounts the selectedBillingAccounts to set
		 */
		public void setSelectedBillingAccounts(String selectedBillingAccounts) {
			this.selectedBillingAccounts = selectedBillingAccounts;
		}

		/**
		 * @return the xmlInvoiceGenerated
		 */
		public Boolean getXmlInvoiceGenerated() {
			return xmlInvoiceGenerated;
		}

		/**
		 * @param xmlInvoiceGenerated the xmlInvoiceGenerated to set
		 */
		public void setXmlInvoiceGenerated(Boolean xmlInvoiceGenerated) {
			this.xmlInvoiceGenerated = xmlInvoiceGenerated;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "BillingRunDto [processDate=" + processDate + ", status=" + status + ", statusDate=" + statusDate + ", billingCycle=" + billingCycle + ", billingAccountNumber=" + billingAccountNumber + ", billableBillingAcountNumber=" + billableBillingAcountNumber + ", producibleInvoiceNumber=" + producibleInvoiceNumber + ", producibleAmountWithoutTax=" + producibleAmountWithoutTax + ", producibleAmountTax=" + producibleAmountTax + ", InvoiceNumber=" + InvoiceNumber
					+ ", producibleAmountWithTax=" + producibleAmountWithTax + ", prAmountWithoutTax=" + prAmountWithoutTax + ", prAmountWithTax=" + prAmountWithTax + ", prAmountTax=" + prAmountTax + ", processType=" + processType + ", startDate=" + startDate + ", endDate=" + endDate + ", invoiceDate=" + invoiceDate + ", lastTransactionDate=" + lastTransactionDate + ", rejectionReason=" + rejectionReason + ", currencyCode=" + currencyCode + ", countryCode=" + countryCode
					+ ", languageCode=" + languageCode + ", selectedBillingAccounts=" + selectedBillingAccounts + ", xmlInvoiceGenerated=" + xmlInvoiceGenerated + "]";
		}
		
		public void setFromEntity(BillingRun billingRunEntity){
			setProcessDate(billingRunEntity.getProcessDate());
			setStatus(billingRunEntity.getStatus());
			setStatusDate(billingRunEntity.getStatusDate());
			setBillingCycle(new BillingCycleDto(billingRunEntity.getBillingCycle()));
			setBillingAccountNumber(billingRunEntity.getBillingAccountNumber());
			setBillableBillingAcountNumber(billingRunEntity.getBillableBillingAcountNumber());
			setProducibleInvoiceNumber(billingRunEntity.getProducibleInvoiceNumber());
			setProducibleAmountWithoutTax(billingRunEntity.getProducibleAmountWithoutTax());
			setProducibleAmountTax(billingRunEntity.getProducibleAmountTax());
			setInvoiceNumber(billingRunEntity.getInvoiceNumber());
			setProducibleAmountWithTax(billingRunEntity.getProducibleAmountWithTax());
			setPrAmountWithoutTax(billingRunEntity.getPrAmountWithoutTax());
			setPrAmountWithTax(billingRunEntity.getPrAmountWithTax());
			setPrAmountTax(billingRunEntity.getPrAmountTax());
			setProcessType(BillingRunTypeEnum.valueOf(billingRunEntity.getProcessType().toString()));
			setStartDate(billingRunEntity.getStartDate());
			setEndDate(billingRunEntity.getEndDate());
			setInvoiceDate(billingRunEntity.getInvoiceDate());
			setLastTransactionDate(billingRunEntity.getLastTransactionDate());
			setRejectionReason(billingRunEntity.getRejectionReason());
			setCurrencyCode(billingRunEntity.getCurrency()==null?null:billingRunEntity.getCurrency().getCurrencyCode());
			setCountryCode(billingRunEntity.getCountry()==null?null:billingRunEntity.getCountry().getCountryCode());
			setLanguageCode(billingRunEntity.getLanguage()==null?null:billingRunEntity.getLanguage().getLanguageCode());
			setSelectedBillingAccounts(billingRunEntity.getSelectedBillingAccounts());
			setXmlInvoiceGenerated(billingRunEntity.getXmlInvoiceGenerated());
		}
	
}
