/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.tax.TaxClass;

/**
 * Aggregated wallet operation.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
public class AggregatedWalletOperation {

	/**
	 * Id of the aggregated entity
	 */
	private Object id;

	/**
	 * Id of the aggregated entity
	 */
	private String code;

	/**
	 * Amount with tax
	 */
	private BigDecimal amountWithoutTax = BigDecimal.ZERO;

	/**
	 * Amount without tax
	 */
	private BigDecimal amountWithTax = BigDecimal.ZERO;

	/**
	 * Amount tax
	 */
	private BigDecimal amountTax = BigDecimal.ZERO;

	/**
	 * Quantity
	 */
	private BigDecimal quantity = BigDecimal.ZERO;

	/**
	 * The tax
	 */
	private Tax tax;

	/**
	 * The invoice sub category
	 */
	private InvoiceSubCategory invoiceSubCategory;

	/**
	 * Use when aggregating by year.
	 */
	private Integer year;

	/**
	 * Use when aggregating by month.
	 */
	private Integer month;

	/**
	 * Use when aggregating by day.
	 */
	private Integer day;

	/**
	 * Id of the seller.
	 */
	private Long sellerId;

	/**
	 * Parameter1.
	 */
	private String parameter1;

	/**
	 * Parameter2.
	 */
	private String parameter2;

	/**
	 * Parameter3.
	 */
	private String parameter3;

	/**
	 * Extra Parameter. This parameter is only use when charging a usage.
	 */
	private String parameterExtra;

	/**
	 * The order number.
	 */
	private String orderNumber;

	/**
	 * Tax class
	 */
	private TaxClass taxClass;

	/**
	 * Unit amount with tax
	 */
	private BigDecimal unitAmountWithTax = BigDecimal.ZERO;
	/**
	 * Unit amount without tax
	 */
	private BigDecimal unitAmountWithoutTax = BigDecimal.ZERO;
	/**
	 * Unit amount tax
	 */
	private BigDecimal unitAmountTax = BigDecimal.ZERO;
	/**
	 * Sorting index
	 */
	private Integer sortIndex;

	private Subscription subscription;
	private ChargeInstance chargeInstance;
	private ServiceInstance serviceInstance;
	private OfferTemplate offer;
	private UserAccount userAccount;
	private BillingAccount billingAccount;
	private Seller seller;
	private Date operationDate;
	private Map<String, Object> cfValues;
	private Date endDate;
	private Date startDate;
	private String description;

	/**
	 * List of wallet operations.
	 */
	private List<Long> walletOperationsIds;

	public AggregatedWalletOperation(String walletOpsIds, Long sellerId, Integer year, Integer month, Integer day, Tax tax, InvoiceSubCategory invoiceSubCategory, Object id,
			BigDecimal amountWithTax, BigDecimal amountWithoutTax, BigDecimal amountTax, TaxClass taxClass, BigDecimal quantity, BigDecimal unitAmountWithoutTax,
			String orderNumber, String parameter1, String parameter2, String parameter3, String parameterExtra, Integer sortIndex) {
		String[] stringIds = walletOpsIds.split(",");
		List<Long> ids = Arrays.asList(stringIds).stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
		this.walletOperationsIds = ids;
		this.sellerId = sellerId;
		this.year = year;
		this.month = month;
		this.day = day;
		this.tax = tax;
		this.invoiceSubCategory = invoiceSubCategory;
		this.id = id;
		this.amountWithTax = amountWithTax;
		this.amountWithoutTax = amountWithoutTax;
		this.amountTax = amountTax;
		MathContext mc = new MathContext(12, RoundingMode.HALF_UP);
		this.unitAmountWithoutTax = (amountWithoutTax.compareTo(BigDecimal.ZERO) != 0 && quantity.compareTo(BigDecimal.ZERO) != 0) ?
				(amountWithoutTax.divide(quantity, mc)) :
				BigDecimal.ZERO;
		this.unitAmountWithTax = (amountWithTax.compareTo(BigDecimal.ZERO) != 0 && quantity.compareTo(BigDecimal.ZERO) != 0) ?
				(amountWithTax.divide(quantity, mc)) :
				BigDecimal.ZERO;
		this.unitAmountTax = this.unitAmountWithTax.subtract(this.unitAmountWithoutTax);
		this.quantity = quantity;
		this.taxClass = taxClass;
		this.orderNumber = orderNumber;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.parameter3 = parameter3;
		this.parameterExtra = parameterExtra;
		this.sortIndex = sortIndex;
	}

	public AggregatedWalletOperation() {

	}

	private Long getComputedId() {
		String strId = getId().toString();
		strId = strId.substring(0, strId.indexOf('|'));

		return Long.parseLong(strId);
	}

	public String getComputedDescription() {
		String strId = getId().toString();
		return strId.substring(strId.indexOf('|') + 1);
	}

	public Long getIdAsLong() {
		return id.toString().contains("|") ? getComputedId() : Long.parseLong(id.toString());
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public String getParameter3() {
		return parameter3;
	}

	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}

	public String getParameterExtra() {
		return parameterExtra;
	}

	public void setParameterExtra(String parameterExtra) {
		this.parameterExtra = parameterExtra;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * @return the walletOperationsIds
	 */
	public List<Long> getWalletOperationsIds() {
		if (walletOperationsIds == null) {
			String[] stringIds = id.toString().split(",");
			walletOperationsIds = Arrays.asList(stringIds).stream().map(x -> new Long(x)).collect(Collectors.toList());
		}
		return walletOperationsIds;
	}

	/**
	 * @param walletOperationsIds the walletOperationsIds to set
	 */
	public void setWalletOperationsIds(List<Long> walletOperationsIds) {
		this.walletOperationsIds = walletOperationsIds;
	}

	/**
	 * @return the taxClass
	 */
	public TaxClass getTaxClass() {
		return taxClass;
	}

	/**
	 * @param taxClass the taxClass to set
	 */
	public void setTaxClass(TaxClass taxClass) {
		this.taxClass = taxClass;
	}

	/**
	 * @return the unitAmountWithTax
	 */
	public BigDecimal getUnitAmountWithTax() {
		return unitAmountWithTax;
	}

	/**
	 * @param unitAmountWithTax the unitAmountWithTax to set
	 */
	public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
		this.unitAmountWithTax = unitAmountWithTax;
	}

	/**
	 * @return the unitAmountWithoutTax
	 */
	public BigDecimal getUnitAmountWithoutTax() {
		return unitAmountWithoutTax;
	}

	/**
	 * @param unitAmountWithoutTax the unitAmountWithoutTax to set
	 */
	public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
		this.unitAmountWithoutTax = unitAmountWithoutTax;
	}

	/**
	 * @return the unitAmountTax
	 */
	public BigDecimal getUnitAmountTax() {
		return unitAmountTax;
	}

	/**
	 * @param unitAmountTax the unitAmountTax to set
	 */
	public void setUnitAmountTax(Double unitAmountTax) {
		this.unitAmountTax = BigDecimal.valueOf(unitAmountTax);
	}

	/**
	 * @return Sorting index
	 */
	public Integer getSortIndex() {
		return sortIndex;
	}

	/**
	 * @param sortIndex The sorting index
	 */
	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public ChargeInstance getChargeInstance() {
		return chargeInstance;
	}

	public void setChargeInstance(ChargeInstance chargeInstance) {
		this.chargeInstance = chargeInstance;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public OfferTemplate getOffer() {
		return offer;
	}

	public void setOffer(OfferTemplate offer) {
		this.offer = offer;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setUnitAmountTax(BigDecimal unitAmountTax) {
		this.unitAmountTax = unitAmountTax;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public Map<String, Object> getCfValues() {
		return cfValues;
	}

	public void setCfValues(Map<String, Object> cfValues) {
		this.cfValues = cfValues;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
