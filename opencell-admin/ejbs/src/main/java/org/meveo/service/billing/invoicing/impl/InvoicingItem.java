package org.meveo.service.billing.invoicing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.meveo.model.billing.InvoiceAgregate;

public class InvoicingItem {

	private Long billingAccountId;
	private Long sellerId;
	private Long count=0l;
	private Long invoiceSubCategoryId;
	private Long userAccountId;
	private Long walletId;
	private Long walletTemplateId;
	private Long taxId;
	private BigDecimal amountWithoutTax = BigDecimal.ZERO;
	private BigDecimal amountTax = BigDecimal.ZERO;
	private BigDecimal amountWithTax = BigDecimal.ZERO;
	private List<Long> rtIDs = new ArrayList<>();
	private String invoiceCategoryId;
	private BigDecimal positiveRTsAmount = null;

	public InvoicingItem(Object[] fields) {
		int i = 0;
		this.billingAccountId = (Long) fields[i++];
		this.sellerId = (Long) fields[i++];
		this.walletId = (Long) fields[i++];
		this.walletTemplateId = (Long) fields[i++];
		this.invoiceSubCategoryId = (Long) fields[i++];
		this.userAccountId = (Long) fields[i++];
		this.taxId = (Long) fields[i++];
		this.amountWithoutTax = (BigDecimal) fields[i++];
		this.amountWithTax = (BigDecimal) fields[i++];
		this.amountTax = (BigDecimal) fields[i++];
		this.count = (Long) fields[i++];
		this.rtIDs =  Pattern.compile(",").splitAsStream((String) fields[i++]).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
		if(fields.length>i) {
			this.setPositiveRTsAmount((BigDecimal) fields[i++]);
		}
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
	 * @return the invoiceCategoryId
	 */
	public String getInvoiceCategoryId() {
		return invoiceCategoryId;
	}

	/**
	 * @param invoiceCategoryId the invoiceCategoryId to set
	 */
	public void setInvoiceCategoryId(String invoiceCategoryId) {
		this.invoiceCategoryId = invoiceCategoryId;
	}

	/**
	 * @return the positiveRTsAmount
	 */
	public BigDecimal getPositiveRTsAmount() {
		return positiveRTsAmount;
	}

	/**
	 * @param positiveRTsAmount the positiveRTsAmount to set
	 */
	public void setPositiveRTsAmount(BigDecimal positiveRTsAmount) {
		this.positiveRTsAmount = positiveRTsAmount;
	}

}