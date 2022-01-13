package org.meveo.service.billing.invoicing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.meveo.model.payments.PaymentMethodEnum;

public class BillingAccountDetailsItem {
	
	private Long billingAccountId;
	private Long tradingLanguageId;
	private Date nextInvoiceDate;
	private Boolean electronicBillingEnabled;
	private String caDueDateDelayEL;
	private Boolean exoneratedFromTaxes;
	private String  exonerationTaxEl;
	private Long paymentMethodId;
	private PaymentMethodEnum paymentMethodType;
	private BigDecimal dueBalance=BigDecimal.ZERO;
	private List<InvoicingItem> invoicingItems;
	
	public BillingAccountDetailsItem(Object[] fields) {
		int i = 0;
		this.billingAccountId = (Long) fields[i++];
		this.tradingLanguageId = (Long) fields[i++];
		this.nextInvoiceDate = (Date) fields[i++];
		this.electronicBillingEnabled= (Boolean) fields[i++];
		this.caDueDateDelayEL= (String) fields[i++];
		this.exoneratedFromTaxes= (Boolean) fields[i++];
		this.exonerationTaxEl= (String) fields[i++];
		this.paymentMethodId = (Long) fields[i++];
		this.paymentMethodType = (PaymentMethodEnum) fields[i++];
		//this.orderDueDateDelayEL = (String) fields[i++];
	}

	/**
	 * @return the billingAccountId
	 */
	public Long getBillingAccountId() {
		return billingAccountId;
	}

	/**
	 * @param billingAccountId the billingAccountId to set
	 */
	public void setBillingAccountId(Long billingAccountId) {
		this.billingAccountId = billingAccountId;
	}

	/**
	 * @return the tradingLanguageId
	 */
	public Long getTradingLanguageId() {
		return tradingLanguageId;
	}

	/**
	 * @param tradingLanguageId the tradingLanguageId to set
	 */
	public void setTradingLanguageId(Long tradingLanguageId) {
		this.tradingLanguageId = tradingLanguageId;
	}

	/**
	 * @return the nextInvoiceDate
	 */
	public Date getNextInvoiceDate() {
		return nextInvoiceDate;
	}

	/**
	 * @param nextInvoiceDate the nextInvoiceDate to set
	 */
	public void setNextInvoiceDate(Date nextInvoiceDate) {
		this.nextInvoiceDate = nextInvoiceDate;
	}

	/**
	 * @return the electronicBillingEnabled
	 */
	public Boolean getElectronicBillingEnabled() {
		return electronicBillingEnabled;
	}

	/**
	 * @param electronicBillingEnabled the electronicBillingEnabled to set
	 */
	public void setElectronicBillingEnabled(Boolean electronicBillingEnabled) {
		this.electronicBillingEnabled = electronicBillingEnabled;
	}

	/**
	 * @return the caDueDateDelayEL
	 */
	public String getCaDueDateDelayEL() {
		return caDueDateDelayEL;
	}

	/**
	 * @param caDueDateDelayEL the caDueDateDelayEL to set
	 */
	public void setCaDueDateDelayEL(String caDueDateDelayEL) {
		this.caDueDateDelayEL = caDueDateDelayEL;
	}

	/**
	 * @return the exoneratedFromTaxes
	 */
	public Boolean getExoneratedFromTaxes() {
		return exoneratedFromTaxes;
	}

	/**
	 * @param exoneratedFromTaxes the exoneratedFromTaxes to set
	 */
	public void setExoneratedFromTaxes(Boolean exoneratedFromTaxes) {
		this.exoneratedFromTaxes = exoneratedFromTaxes;
	}

	/**
	 * @return the exonerationTaxEl
	 */
	public String getExonerationTaxEl() {
		return exonerationTaxEl;
	}

	/**
	 * @param exonerationTaxEl the exonerationTaxEl to set
	 */
	public void setExonerationTaxEl(String exonerationTaxEl) {
		this.exonerationTaxEl = exonerationTaxEl;
	}

	/**
	 * @return the paymentMethodId
	 */
	public Long getPaymentMethodId() {
		return paymentMethodId;
	}

	/**
	 * @param paymentMethodId the paymentMethodId to set
	 */
	public void setPaymentMethodId(Long paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	/**
	 * @return the paymentMethodType
	 */
	public PaymentMethodEnum getPaymentMethodType() {
		return paymentMethodType;
	}

	/**
	 * @param paymentMethodType the paymentMethodType to set
	 */
	public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
		this.paymentMethodType = paymentMethodType;
	}

	/**
	 * @return the dueBalance
	 */
	public BigDecimal getDueBalance() {
		return dueBalance;
	}

	/**
	 * @param dueBalance the dueBalance to set
	 */
	public void setDueBalance(BigDecimal dueBalance) {
		this.dueBalance = dueBalance;
	}

	/**
	 * @return the invoicingItems
	 */
	public List<InvoicingItem> getInvoicingItems() {
		return invoicingItems;
	}

	/**
	 * @param invoicingItems the invoicingItems to set
	 */
	public void setInvoicingItems(List<InvoicingItem> invoicingItems) {
		this.invoicingItems = invoicingItems;
	}


}