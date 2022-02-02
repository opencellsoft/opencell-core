package org.meveo.service.billing.impl;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicStatistics {

    private Map<Long, List<Long>> iLIdsRtIdsCorrespondence;
    private BigDecimal sumAmountWithoutTax;
    private BigDecimal sumAmountWithTax;
    private Integer billableEntitiesCount;

    public BasicStatistics() {
        this.iLIdsRtIdsCorrespondence = new HashMap<>();
        this.sumAmountWithoutTax = ZERO;
        this.sumAmountWithTax = ZERO;
        this.billableEntitiesCount = valueOf(0);
    }

    public Map<Long, List<Long>> getiLIdsRtIdsCorrespondence() {
        return iLIdsRtIdsCorrespondence;
    }

    public void setiLIdsRtIdsCorrespondence(Map<Long, List<Long>> iLIdsRtIdsCorrespondence) {
        this.iLIdsRtIdsCorrespondence = iLIdsRtIdsCorrespondence;
    }

    public BigDecimal getSumAmountWithoutTax() {
        return sumAmountWithoutTax;
    }

    public BigDecimal getGetSumAmountWithTax() {
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

    public Integer getBillableEntitiesCount() {
        return billableEntitiesCount;
    }

    public void setBillableEntitiesCount(Integer billableEntitiesCount) {
        this.billableEntitiesCount = billableEntitiesCount;
    }
}