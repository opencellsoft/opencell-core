package org.meveo.service.billing.impl;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

public class BasicStatistics {

    private BigDecimal sumAmountWithoutTax;
    private BigDecimal sumAmountWithTax;
    private BigDecimal sumAmountTax;
    private Integer count;

    public BasicStatistics() {
        this.sumAmountWithoutTax = ZERO;
        this.sumAmountWithTax = ZERO;
        this.sumAmountTax = ZERO;
        this.count = valueOf(0);
    }

    public BigDecimal getSumAmountWithoutTax() {
        return sumAmountWithoutTax;
    }

    public BigDecimal getSumAmountWithTax() {
        return sumAmountWithTax;
    }

    public BigDecimal getSumAmountTax() {
        return sumAmountTax;
    }

    public void setSumAmountWithoutTax(BigDecimal sumAmountWithoutTax) {
        this.sumAmountWithoutTax = sumAmountWithoutTax;
    }

    public void setSumAmountWithTax(BigDecimal sumAmountWithTax) {
        this.sumAmountWithTax = sumAmountWithTax;
    }

    public void setSumAmountTax(BigDecimal sumAmountTax) {
        this.sumAmountTax = sumAmountTax;
    }

    public BigDecimal addToAmountWithoutTax(BigDecimal amount) {
        sumAmountWithoutTax = this.sumAmountWithoutTax.add(amount);
        return sumAmountWithoutTax;
    }

    public BigDecimal addToAmountWithTax(BigDecimal amount) {
        this.sumAmountWithTax = this.sumAmountWithTax.add(amount);
        return sumAmountWithTax;
    }

    public BigDecimal addToAmountTax(BigDecimal amount) {
        this.sumAmountTax = this.sumAmountTax.add(amount);
        return sumAmountTax;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Aggregate statistics
     * 
     * @param statistics Statistics to add
     */
    public synchronized void append(BasicStatistics statistics) {
        this.count += statistics.getCount();
        this.sumAmountWithoutTax = this.sumAmountWithoutTax.add(statistics.getSumAmountWithoutTax());
        this.sumAmountWithTax = this.sumAmountWithTax.add(statistics.getSumAmountWithTax());
        this.sumAmountTax = this.sumAmountTax.add(statistics.getSumAmountTax());
    }
}