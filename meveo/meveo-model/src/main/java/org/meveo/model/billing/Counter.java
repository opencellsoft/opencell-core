package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTemplate;

@Entity
@Table(name="BILLING_COUNTER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_COUNTER_TEMPLATE_SEQ")
public class Counter extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4924601467998738157L;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "COUNTER_TEMPLATE_ID")
	private CounterTemplate counterTemplate;
	
    @ManyToOne
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount userAccount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START_DATE")
    private Date periodStartDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END_DATE")
    private Date periodEndDate;
    
	@Column(name = "VALUE", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal value;

	public CounterTemplate getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(CounterTemplate counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Date getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public Date getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(Date periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	
}
