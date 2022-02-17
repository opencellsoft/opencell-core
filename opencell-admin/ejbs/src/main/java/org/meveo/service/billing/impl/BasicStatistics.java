package org.meveo.service.billing.impl;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicStatistics {

    private Map<Long, List<Long>> iLIdsRtIdsCorrespondence;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;
    private BigDecimal amountTax;
    private Integer billableEntitiesCount;

    public BasicStatistics() {
        this.iLIdsRtIdsCorrespondence = new HashMap<>();
        this.amountWithoutTax = ZERO;
        this.amountWithTax = ZERO;
        this.amountTax = ZERO;
        this.billableEntitiesCount = valueOf(0);
    }

    public Map<Long, List<Long>> getiLIdsRtIdsCorrespondence() {
        return iLIdsRtIdsCorrespondence;
    }

    public void setILIdsRtIdsCorrespondence(Map<Long, List<Long>> iLIdsRtIdsCorrespondence) {
        this.iLIdsRtIdsCorrespondence = iLIdsRtIdsCorrespondence;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public BigDecimal addToAmountWithoutTax(BigDecimal amount) {
        amountWithoutTax = this.amountWithoutTax.add(amount);
        return amountWithoutTax;
    }

    public BigDecimal addToAmountTax(BigDecimal amount) {
        this.amountTax = this.amountTax.add(amount);
        return amountTax;
    }

    public BigDecimal addToAmountWithTax(BigDecimal amount) {
        this.amountWithTax = this.amountWithTax.add(amount);
        return amountWithTax;
    }

    public Integer getBillableEntitiesCount() {
        return billableEntitiesCount;
    }

    public void setBillableEntitiesCount(Integer billableEntitiesCount) {
        this.billableEntitiesCount = billableEntitiesCount;
    }
}