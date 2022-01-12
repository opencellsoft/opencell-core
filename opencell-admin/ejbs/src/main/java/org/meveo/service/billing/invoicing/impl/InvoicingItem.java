package org.meveo.service.billing.invoicing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.payments.PaymentMethodEnum;

public class InvoicingItem {

	private Long billingAccountId;
	private String languageCode;
	private BigDecimal dueBalance=BigDecimal.ZERO;
	private Long billingRunId;
	private Long invoiceTypeId;
	private Long count=0l;
	private Long invoiceSubCategoryId;
	private Long invoiceCategoryId;
	private Long userAccountId;
	private Long walletId;
	private Long walletTemplateId;
	private Long taxId;
	private BigDecimal taxPercent;
	private BigDecimal amountWithoutTax = BigDecimal.ZERO;
	private BigDecimal amountTax = BigDecimal.ZERO;
	private BigDecimal amountWithTax = BigDecimal.ZERO;
	private Long paymentMethodId;
	private PaymentMethodEnum paymentMethodType;
	private String orderDueDateDelayEL; 
	private String caDueDateDelayEL; 
	private String bcDueDateDelayEL;
	private List<Long> rtIDs = new ArrayList<>();
	private Date nextInvoiceDate;
	private Boolean exoneratedFromTaxes;
	private String  exonerationTaxEl;

	private Long sellerId;
	private Boolean electronicBillingEnabled;

	
	public InvoicingItem(long baId, long sellerId, long walletId, long walletTemplateId, long scId, long categoryId, long userAccountId, long taxId, BigDecimal percent, long paymentMethodId, PaymentMethodEnum paymentType, String languageCode, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax) {
		this.billingAccountId=baId;
		this.sellerId=sellerId; 
		this.walletId=walletId;
		this.invoiceSubCategoryId = scId;
		this.userAccountId=userAccountId; 
		this.taxId=taxId; 
		this.paymentMethodId=paymentMethodId;
		this.paymentMethodType = paymentType;
		this.languageCode=languageCode;
		this.amountWithoutTax=amountWithoutTax;
		this.amountWithTax=amountWithTax;
		this.amountTax=amountTax;
		this.taxPercent=percent;
		this.walletTemplateId=walletTemplateId;
		this.invoiceCategoryId=categoryId;
	}
	
	public InvoicingItem(Object[] fields) {
		int i = 0;
		this.billingAccountId = (Long) fields[i++];
		this.sellerId = (Long) fields[i++];
		this.walletId = (Long) fields[i++];
		this.walletTemplateId = (Long) fields[i++];
		this.invoiceSubCategoryId = (Long) fields[i++];
		this.invoiceCategoryId = (Long) fields[i++];
		this.userAccountId = (Long) fields[i++];
		this.taxId = (Long) fields[i++];
		this.taxPercent = (BigDecimal) fields[i++];
		this.paymentMethodId = (Long) fields[i++];
		this.paymentMethodType = (PaymentMethodEnum) fields[i++];
		this.languageCode = (String) fields[i++];
		//this.orderDueDateDelayEL = (String) fields[i++];
		this.caDueDateDelayEL = (String) fields[i++];
		this.amountWithoutTax = (BigDecimal) fields[i++];
		this.amountWithTax = (BigDecimal) fields[i++];
		this.amountTax = (BigDecimal) fields[i++];
		this.rtIDs =  Pattern.compile(",").splitAsStream((String) fields[i++]).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
		this.count = (long) rtIDs.size();
		this.nextInvoiceDate = (Date) fields[i++];
		this.exoneratedFromTaxes= (Boolean) fields[i++];
		this.exonerationTaxEl= (String) fields[i++];
		this.electronicBillingEnabled= (Boolean) fields[i++];
	}

	/**
	 * @param items
	 */
	public InvoicingItem(List<InvoicingItem> items) {
		for (InvoicingItem item : items) {
			this.rtIDs.addAll(item.getrtIDs());
			this.count=this.count+item.count;
			this.amountTax = this.amountTax.add(item.getAmountTax());
			this.amountWithTax = this.amountWithTax.add(item.getAmountWithTax());
			this.amountWithoutTax = this.amountWithoutTax.add(item.getAmountWithoutTax());
		}
	}

	public List<Long> getrtIDs() {
		return rtIDs;
	}

	public Long getBillingRunId() {
		return billingRunId;
	}

	public void setBillingRunId(Long billingRunId) {
		this.billingRunId = billingRunId;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public Long getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(Long paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public PaymentMethodEnum getPaymentMethodType() {
		return paymentMethodType;
	}

	public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
		this.paymentMethodType = paymentMethodType;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Long getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(Long invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}

	public BigDecimal getDueBalance() {
		return dueBalance;
	}

	public void setDueBalance(BigDecimal dueBalance) {
		this.dueBalance = dueBalance;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public long getBillingAccountId() {
		return billingAccountId;
	}

	public void setBillingAccountId(long billingAccountId) {
		this.billingAccountId = billingAccountId;
	}

	public Long getUserAccountId() {
		return userAccountId;
	}

	public void setUserAccountId(Long userAccountId) {
		this.userAccountId = userAccountId;
	}

	public Long getWalletId() {
		return walletId;
	}

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public Long getWalletTemplateId() {
		return walletTemplateId;
	}

	public void setWalletTemplateId(Long walletTemplateId) {
		this.walletTemplateId = walletTemplateId;
	}

	public String getScaKey() {
		return "" + getUserAccountId() + "_" + getWalletId() + "_" + invoiceSubCategoryId;
	}

	public String getCaKey() {
		return "" + getUserAccountId() + "_" + invoiceCategoryId;
	}

	public String getInvoiceKey() {
		return "" + billingAccountId + sellerId + isPrepaid();
	}


	public Long getTaxId() {
		return taxId;
	}

	public void setTaxId(Long taxId) {
		this.taxId = taxId;
	}

	public boolean isPrepaid() {
		return walletTemplateId != null;
	}

	public void addAmounts(InvoiceAgregate discountAggregate) {
		this.count=this.count+discountAggregate.getItemNumber();
		this.amountTax = this.amountTax.add(discountAggregate.getAmountTax());
		this.amountWithoutTax = this.amountWithoutTax.add(discountAggregate.getAmountWithoutTax());
		this.amountWithTax = this.amountWithTax.add(discountAggregate.getAmountWithTax());
	}

	public BigDecimal getAmount(boolean isEnterprise) {
		return isEnterprise ? getAmountWithoutTax() : getAmountWithTax();
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	/**
	 * @return the invoiceCategoryId
	 */
	public long getInvoiceCategoryId() {
		return invoiceCategoryId;
	}

	/**
	 * @param invoiceCategoryId the invoiceCategoryId to set
	 */
	public void setInvoiceCategoryId(long invoiceCategoryId) {
		this.invoiceCategoryId = invoiceCategoryId;
	}

	/**
	 * @return the invoiceSubCategoryId
	 */
	public long getInvoiceSubCategoryId() {
		return invoiceSubCategoryId;
	}

	/**
	 * @param invoiceSubCategoryId the invoiceSubCategoryId to set
	 */
	public void setInvoiceSubCategoryId(long invoiceSubCategoryId) {
		this.invoiceSubCategoryId = invoiceSubCategoryId;
	}

	/**
	 * @return
	 */
	public Integer getCount() {
		return count.intValue();
	}

	/**
	 * @return the orderDueDateDelayEL
	 */
	public String getOrderDueDateDelayEL() {
		return orderDueDateDelayEL;
	}

	/**
	 * @param orderDueDateDelayEL the orderDueDateDelayEL to set
	 */
	public void setOrderDueDateDelayEL(String orderDueDateDelayEL) {
		this.orderDueDateDelayEL = orderDueDateDelayEL;
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
	 * @return the bcDueDateDelayEL
	 */
	public String getBcDueDateDelayEL() {
		return bcDueDateDelayEL;
	}

	/**
	 * @param bcDueDateDelayEL the bcDueDateDelayEL to set
	 */
	public void setBcDueDateDelayEL(String bcDueDateDelayEL) {
		this.bcDueDateDelayEL = bcDueDateDelayEL;
	}

	/**
	 * @param billingAccountId the billingAccountId to set
	 */
	public void setBillingAccountId(Long billingAccountId) {
		this.billingAccountId = billingAccountId;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}

	/**
	 * @param invoiceSubCategoryId the invoiceSubCategoryId to set
	 */
	public void setInvoiceSubCategoryId(Long invoiceSubCategoryId) {
		this.invoiceSubCategoryId = invoiceSubCategoryId;
	}

	/**
	 * @param invoiceCategoryId the invoiceCategoryId to set
	 */
	public void setInvoiceCategoryId(Long invoiceCategoryId) {
		this.invoiceCategoryId = invoiceCategoryId;
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
	 * @return
	 */
	public Boolean isElectronicBillingEnabled() {
		return electronicBillingEnabled;
	}

}