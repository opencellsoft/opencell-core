package org.meveo.admin.job.invoicing;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.meveo.model.billing.InvoiceAgregate;
public class InvoicingItem {
	private Long billingAccountId;
	private Long count = 0l;
	private Long invoiceSubCategoryId;
	private Long userAccountId;
	private Long taxId;
	private BigDecimal amountWithoutTax = BigDecimal.ZERO;
	private BigDecimal amountTax = BigDecimal.ZERO;
	private BigDecimal amountWithTax = BigDecimal.ZERO;
	private List<Long> ilIDs = new ArrayList<>();
	private String invoiceCategoryId;
	private String invoiceKey;

	public InvoicingItem(Object[] fields) {
		int i = 0;
		this.billingAccountId = (Long) fields[i++];
		this.invoiceSubCategoryId = (Long) fields[i++];
		this.userAccountId = (Long) fields[i++];
		this.taxId = (Long) fields[i++];
		this.amountWithoutTax = (BigDecimal) fields[i++];
		this.amountWithTax = (BigDecimal) fields[i++];
		this.amountTax = (BigDecimal) fields[i++];
		this.count = (Long) fields[i++];
		this.ilIDs = Pattern.compile(",").splitAsStream((String) fields[i++]).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
		this.invoiceKey = (String) fields[i++];
	}
	/**
	 * @param items
	 */
	public InvoicingItem(List<InvoicingItem> items) {
		for (InvoicingItem item : items) {
			this.ilIDs.addAll(item.getilIDs());
			this.count = this.count + item.count;
			this.amountTax = this.amountTax.add(item.getAmountTax());
			this.amountWithTax = this.amountWithTax.add(item.getAmountWithTax());
			this.amountWithoutTax = this.amountWithoutTax.add(item.getAmountWithoutTax());
		}
	}
	public List<Long> getilIDs() {
		return ilIDs;
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
	public String getScaKey() {
		return "" + getUserAccountId() + "_" +  + invoiceSubCategoryId;
	}
	public String getCaKey() {
		return "" + getUserAccountId() + "_" + invoiceCategoryId;
	}
	public Long getTaxId() {
		return taxId;
	}
	public void setTaxId(Long taxId) {
		this.taxId = taxId;
	}
	public void addAmounts(InvoiceAgregate discountAggregate) {
		this.count = this.count + discountAggregate.getItemNumber();
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

	public String getInvoiceKey() {
		return invoiceKey;
	}

	public void setInvoiceKey(String invoiceKey) {
		this.invoiceKey = invoiceKey;
	}

	@Override
	public String toString() {
		return "InvoicingItem [billingAccountId : " + billingAccountId + ", invoiceSubCategoryId : " + invoiceSubCategoryId
				+ ", userAccountId : " + userAccountId + ", amountWithoutTax : " + amountWithoutTax
				+ ", amountTax : " + amountTax + ", amountWithTax : " + amountWithTax  + ", invoiceCategoryId : " + invoiceCategoryId
				+ ", invoiceKey : " + invoiceKey + "]";
	}
}