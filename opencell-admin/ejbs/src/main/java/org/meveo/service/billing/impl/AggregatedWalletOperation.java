package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;

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
	 * List of wallet operations.
	 */
	private List<Long> walletOperationsIds;

	public AggregatedWalletOperation(String walletOpsIds, Long sellerId, Integer year, Integer month, Integer day, Tax tax,
			InvoiceSubCategory invoiceSubCategory, Object id, BigDecimal amountWithTax, BigDecimal amountWithoutTax,
			BigDecimal amountTax, BigDecimal quantity, String orderNumber, String parameter1, String parameter2, String parameter3,
			String parameterExtra) {
		String[] stringIds = walletOpsIds.split(",");
		List<Long> ids = Arrays.asList(stringIds).stream().map(x-> new Long(x)).collect(Collectors.toList());
		this.walletOperationsIds=ids;
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
		this.quantity = quantity;
		this.orderNumber = orderNumber;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.parameter3 = parameter3;
		this.parameterExtra = parameterExtra;
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
		return walletOperationsIds;
	}

	/**
	 * @param walletOperationsIds the walletOperationsIds to set
	 */
	public void setWalletOperationsIds(List<Long> walletOperationsIds) {
		this.walletOperationsIds = walletOperationsIds;
	}

}
