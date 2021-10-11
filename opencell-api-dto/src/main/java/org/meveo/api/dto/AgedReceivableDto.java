package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.Date;

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
	private Date dueDate;

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

}
