package org.meveo.model.billing;

import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;

import java.math.BigDecimal;
import java.util.Map;

public class MinAmountData {
    private BigDecimal minAmount;
    private String minAmountLabel;
    private Amounts amounts;
    private Map<Long, Amounts> invoiceSubCategoryAmounts;
    private IEntity entity;
    private Seller seller;

    public MinAmountData(BigDecimal minAmount, String minAmountLabel, Amounts amounts, Map<Long, Amounts> invoiceSubCategoryAmounts, IEntity entity, Seller seller) {
        this.minAmount = minAmount;
        this.minAmountLabel = minAmountLabel;
        this.amounts = amounts;
        this.invoiceSubCategoryAmounts = invoiceSubCategoryAmounts;
        this.entity = entity;
        this.seller = seller;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public String getMinAmountLabel() {
        return minAmountLabel;
    }

    public void setMinAmountLabel(String minAmountLabel) {
        this.minAmountLabel = minAmountLabel;
    }

    public Amounts getAmounts() {
        return amounts;
    }

    public void setAmounts(Amounts amounts) {
        this.amounts = amounts;
    }

    public Map<Long, Amounts> getInvoiceSubCategoryAmounts() {
        return invoiceSubCategoryAmounts;
    }

    public void setInvoiceSubCategoryAmounts(Map<Long, Amounts> invoiceSubCategoryAmounts) {
        this.invoiceSubCategoryAmounts = invoiceSubCategoryAmounts;
    }

    public IEntity getEntity() {
        return entity;
    }

    public void setEntity(IEntity entity) {
        this.entity = entity;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
