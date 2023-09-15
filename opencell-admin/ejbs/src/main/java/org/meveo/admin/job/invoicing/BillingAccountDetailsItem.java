package org.meveo.admin.job.invoicing;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.meveo.model.payments.PaymentMethodEnum;
public class BillingAccountDetailsItem {
	private Long billingAccountId;
	private Long sellerId;
	private Long tradingLanguageId;
	private Date nextInvoiceDate;
	private Boolean electronicBillingEnabled;
	private String caDueDateDelayEL;
	private Boolean exoneratedFromTaxes;
	private String exonerationTaxEl;
	private Long paymentMethodId;
	private PaymentMethodEnum paymentMethodType;
	private BigDecimal dueBalance = BigDecimal.ZERO;
	private List<List<InvoicingItem>> invoicingItems;
	private String discountPlanInstancesSummary;
	private List<DiscountPlanSummary> discountPlanSummaries;
	private int totalILs;
	public BillingAccountDetailsItem(Object[] fields) {
		int i = 0;
		this.billingAccountId = (Long) fields[i++];
		this.sellerId=(Long) fields[i++];
		this.tradingLanguageId = (Long) fields[i++];
		this.nextInvoiceDate = (Date) fields[i++];
		this.electronicBillingEnabled = (Boolean) fields[i++];
		this.caDueDateDelayEL = (String) fields[i++];
		this.exoneratedFromTaxes = (Boolean) fields[i++];
		this.exonerationTaxEl = (String) fields[i++];
		this.paymentMethodId = (Long) fields[i++];
		this.paymentMethodType = (PaymentMethodEnum) fields[i++];
		Long baPaymentMethodId= (Long) fields[i++];
		PaymentMethodEnum baPaymentMethodType= (PaymentMethodEnum) fields[i++];
		if(baPaymentMethodId!=null) {
			this.paymentMethodId = baPaymentMethodId;
			this.paymentMethodType = baPaymentMethodType;
		}
		this.discountPlanInstancesSummary = (String) fields[i++];
		if (!StringUtils.isEmpty(discountPlanInstancesSummary) && discountPlanInstancesSummary.length() > 2) {
			discountPlanSummaries = Stream.of(discountPlanInstancesSummary.split("\\,", -1)).distinct().filter(x->x.length()>2)
					.map(x -> new DiscountPlanSummary(x)).collect(Collectors.toList());
		}
		this.dueBalance = (BigDecimal) Optional.ofNullable((BigDecimal) fields[i++]).orElse(BigDecimal.ZERO);
	}
	public Long getBillingAccountId() {
		return billingAccountId;
	}
	public void setBillingAccountId(Long billingAccountId) {
		this.billingAccountId = billingAccountId;
	}
	public Long getTradingLanguageId() {
		return tradingLanguageId;
	}
	public void setTradingLanguageId(Long tradingLanguageId) {
		this.tradingLanguageId = tradingLanguageId;
	}
	public Date getNextInvoiceDate() {
		return nextInvoiceDate;
	}
	public void setNextInvoiceDate(Date nextInvoiceDate) {
		this.nextInvoiceDate = nextInvoiceDate;
	}
	public Boolean getElectronicBillingEnabled() {
		return electronicBillingEnabled;
	}
	public void setElectronicBillingEnabled(Boolean electronicBillingEnabled) {
		this.electronicBillingEnabled = electronicBillingEnabled;
	}
	public String getCaDueDateDelayEL() {
		return caDueDateDelayEL;
	}
	public void setCaDueDateDelayEL(String caDueDateDelayEL) {
		this.caDueDateDelayEL = caDueDateDelayEL;
	}
	public Boolean getExoneratedFromTaxes() {
		return exoneratedFromTaxes;
	}
	public void setExoneratedFromTaxes(Boolean exoneratedFromTaxes) {
		this.exoneratedFromTaxes = exoneratedFromTaxes;
	}
	public String getExonerationTaxEl() {
		return exonerationTaxEl;
	}
	public void setExonerationTaxEl(String exonerationTaxEl) {
		this.exonerationTaxEl = exonerationTaxEl;
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
	public BigDecimal getDueBalance() {
		return dueBalance;
	}
	public void setDueBalance(BigDecimal dueBalance) {
		this.dueBalance = dueBalance;
	}
	public List<List<InvoicingItem>> getInvoicingItems() {
		return invoicingItems;
	}
	public void setInvoicingItems(List<List<InvoicingItem>> invoicingItems) {
		this.totalILs = (invoicingItems.stream().flatMap(List::stream).collect(Collectors.toList())).stream()
				.mapToInt(InvoicingItem::getCount).sum();
		this.invoicingItems = invoicingItems;
	}
	public List<DiscountPlanSummary> getdiscountPlanSummaries() {
		return discountPlanSummaries;
	}
	public void setdiscountPlanSummaries(List<DiscountPlanSummary> discountPlanSummaries) {
		this.discountPlanSummaries = discountPlanSummaries;
	}
	public int getTotalILs() {
		return totalILs;
	}
	public void setTotalILs(int totalILs) {
		this.totalILs = totalILs;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
}