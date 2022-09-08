package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.meveo.model.BaseEntity;
import org.meveo.model.payments.DunningLevelEnum;

public class AgedReceivableDto extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String customerAccountCode;
	private BigDecimal notYetDue;
	private BigDecimal sum1To30;
	private BigDecimal sum31To60;
	private BigDecimal sum61To90;
	private BigDecimal sum90Up;
	private BigDecimal generalTotal;
	private DunningLevelEnum dunningLevel;
	private String customerAccountName;
	private String customerAccountDescription;
	private String sellerName;
	private String sellerCode;
	private Date dueDate;
	private List<BigDecimal> netAmountByPeriod;
	private List<BigDecimal> taxAmountByPeriod;
	private List<BigDecimal> totalAmountByPeriod;
	private String funcCurrency;
	private Long invoiceId;
	private String invoiceNumber;
	private String tradingCurrency;
	private BigDecimal billedAmount;
	private Long customerId;

	public AgedReceivableDto() {
		super();
	}

	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}

	public BigDecimal getNotYetDue() {
		return notYetDue;
	}

	public void setNotYetDue(BigDecimal notYetDue) {
		this.notYetDue = notYetDue;
	}

	public BigDecimal getSum1To30() {
		return sum1To30;
	}

	public void setSum1To30(BigDecimal sum1To30) {
		this.sum1To30 = sum1To30;
	}

	public BigDecimal getSum31To60() {
		return sum31To60;
	}

	public void setSum31To60(BigDecimal sum31To60) {
		this.sum31To60 = sum31To60;
	}

	public BigDecimal getSum61To90() {
		return sum61To90;
	}

	public void setSum61To90(BigDecimal sum61To90) {
		this.sum61To90 = sum61To90;
	}

	public BigDecimal getSum90Up() {
		return sum90Up;
	}

	public void setSum90Up(BigDecimal sum90Up) {
		this.sum90Up = sum90Up;
	}

	public BigDecimal getGeneralTotal() {
		return generalTotal;
	}

	public void setGeneralTotal(BigDecimal generalTotal) {
		this.generalTotal = generalTotal;
	}

	/**
	 * @return the dunningLevel
	 */
	public DunningLevelEnum getDunningLevel() {
		return dunningLevel;
	}

	/**
	 * @param dunningLevel the dunningLevel to set
	 */
	public void setDunningLevel(DunningLevelEnum dunningLevel) {
		this.dunningLevel = dunningLevel;
	}

	/**
	 * @return the customerAccountName
	 */
	public String getCustomerAccountName() {
		return customerAccountName;
	}

	/**
	 * @param customerAccountName the customerAccountName to set
	 */
	public void setCustomerAccountName(String customerAccountName) {
		this.customerAccountName = customerAccountName;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getCustomerAccountDescription() {
		return customerAccountDescription;
	}

	public void setCustomerAccountDescription(String customerAccountDescription) {
		this.customerAccountDescription = customerAccountDescription;
	}

	public List<BigDecimal> getNetAmountByPeriod() {
		return netAmountByPeriod;
	}

	public void setNetAmountByPeriod(List<BigDecimal> netAmountByPeriod) {
		this.netAmountByPeriod = netAmountByPeriod;
	}

	public List<BigDecimal> getTaxAmountByPeriod() {
		return taxAmountByPeriod;
	}

	public void setTaxAmountByPeriod(List<BigDecimal> taxAmountByPeriod) {
		this.taxAmountByPeriod = taxAmountByPeriod;
	}

	public List<BigDecimal> getTotalAmountByPeriod() {
		return totalAmountByPeriod;
	}

	public void setTotalAmountByPeriod(List<BigDecimal> totalAmountByPeriod) {
		this.totalAmountByPeriod = totalAmountByPeriod;
	}

	public String getFuncCurrency() {
		return funcCurrency;
	}

	public void setFuncCurrency(String funcCurrency) {
		this.funcCurrency = funcCurrency;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(String tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public BigDecimal getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(BigDecimal billedAmount) {
		this.billedAmount = billedAmount;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
}