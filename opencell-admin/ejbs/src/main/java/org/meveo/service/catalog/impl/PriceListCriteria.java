package org.meveo.service.catalog.impl;

import org.meveo.model.payments.PaymentMethodEnum;

public class PriceListCriteria {

	private Long offset;
	private Long limit;
	private String sortOrder;
	private String sortBy;
	private Long brandId;
	private Long customerCategoryId;
	private Long creditCategoryId;
	private Long countryId;
	private Long currencyId;
	private Long titleId;
	private PaymentMethodEnum paymentMethodEnum;
	private Long sellerId;
	private Long attachedPriceListId;

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public Long getCustomerCategoryId() {
		return customerCategoryId;
	}

	public void setCustomerCategoryId(Long customerCategoryId) {
		this.customerCategoryId = customerCategoryId;
	}

	public Long getCreditCategoryId() {
		return creditCategoryId;
	}

	public void setCreditCategoryId(Long creditCategoryId) {
		this.creditCategoryId = creditCategoryId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Long getTitleId() {
		return titleId;
	}

	public void setTitleId(Long titleId) {
		this.titleId = titleId;
	}

	public PaymentMethodEnum getPaymentMethodEnum() {
		return paymentMethodEnum;
	}

	public void setPaymentMethodEnum(PaymentMethodEnum paymentMethodEnum) {
		this.paymentMethodEnum = paymentMethodEnum;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Long getAttachedPriceListId() {
		return attachedPriceListId;
	}

	public void setAttachedPriceListId(Long attachedPriceListId) {
		this.attachedPriceListId = attachedPriceListId;
	}
}
