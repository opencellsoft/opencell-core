package org.meveo.service.billing.impl;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

public class BasicStatistics {

    private BigDecimal sumAmountWithoutTax;
    private BigDecimal sumAmountWithTax;
    private Integer count;

    public BasicStatistics() {
        this.sumAmountWithoutTax = ZERO;
        this.sumAmountWithTax = ZERO;
        this.count = valueOf(0);
    }


    public BigDecimal getSumAmountWithoutTax() {
        return sumAmountWithoutTax;
    }

    public BigDecimal getSumAmountWithTax() {
        return sumAmountWithTax;
    }

    public BigDecimal addToAmountWithoutTax(BigDecimal amount) {
        sumAmountWithoutTax = this.sumAmountWithoutTax.add(amount);
        return sumAmountWithoutTax;
    }

    public BigDecimal addToAmountWithTax(BigDecimal amount) {
        this.sumAmountWithTax = this.sumAmountWithTax.add(amount);
        return sumAmountWithTax;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

	/**
	 * @param statistics
	 */
	public void append(BasicStatistics statistics) {
		this.count+=statistics.getCount();
		 this.sumAmountWithoutTax=this.sumAmountWithoutTax.add(statistics.getSumAmountWithoutTax());
	     this.sumAmountWithTax=this.sumAmountWithTax.add(statistics.getSumAmountWithTax());
	}
}