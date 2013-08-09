package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTypeEnum;

@Entity
@Table(name = "BILLING_COUNTER_PERIOD")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_COUNTER_PERIOD_SEQ")
public class CounterPeriod extends BusinessEntity {
	private static final long serialVersionUID = -4924601467998738157L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTER_INSTANCE_ID")
	private CounterInstance counterInstance;

	@Enumerated(EnumType.STRING)
	@Column(name = "COUNTER_TYPE")
	private CounterTypeEnum counterType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PERIOD_START_DATE")
	private Date periodStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PERIOD_END_DATE")
	private Date periodEndDate;

	@Column(name = "LEVEL_NUM", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal level;

	@Column(name = "VALUE", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal value;

	public CounterInstance getCounterInstance() {
		return counterInstance;
	}

	public void setCounterInstance(CounterInstance counterInstance) {
		this.counterInstance = counterInstance;
	}

	public CounterTypeEnum getCounterType() {
		return counterType;
	}

	public void setCounterType(CounterTypeEnum counterType) {
		this.counterType = counterType;
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

	public BigDecimal getLevel() {
		return level;
	}

	public void setLevel(BigDecimal level) {
		this.level = level;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
