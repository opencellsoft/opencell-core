package org.meveo.model.cache;

import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;

public class CounterPeriodCache {

    private CounterTypeEnum counterType;
	private Date startDate;
	private Date endDate;
	private BigDecimal value;
	private BigDecimal level;
	
	public CounterTypeEnum getCounterType() {
		return counterType;
	}
	public void setCounterType(CounterTypeEnum counterType) {
		this.counterType = counterType;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public BigDecimal getLevel() {
		return level;
	}
	public void setLevel(BigDecimal level) {
		this.level = level;
	}
	
	public static CounterPeriodCache getInstance(CounterPeriod counterPeriod,CounterTemplate template) {
		CounterPeriodCache cacheValue = new CounterPeriodCache();
		cacheValue.counterType=template.getCounterType();
		cacheValue.endDate=counterPeriod.getPeriodEndDate();
		cacheValue.level=template.getLevel();
		cacheValue.startDate=counterPeriod.getPeriodStartDate();
		cacheValue.value=counterPeriod.getValue();
		return cacheValue;
	}
	
}
