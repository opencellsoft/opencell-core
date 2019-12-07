package org.meveo.model.billing;

import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Store needed data related to create a new Rated transaction from minimum amount
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
public class MinAmountData {
    /**
     * The minimum amount
     */
    private BigDecimal minAmount;
    /**
     * The minimum amounts label
     */
    private String minAmountLabel;
    /**
     * The amounts
     */
    private Amounts amounts;
    /**
     * Min amount by invoiceSubCateory
     */
    private Map<Long, Amounts> invoiceSubCategoryAmounts;

    /**
     * The entity having the minimum amount, customer, CA, BA, UA, subscription or serviceInstance
     */
    private BusinessEntity entity;

    /**
     * The seller
     */
    private Seller seller;

    public MinAmountData(BigDecimal minAmount, String minAmountLabel, Amounts amounts, Map<Long, Amounts> invoiceSubCategoryAmounts, BusinessEntity entity, Seller seller) {
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

    public BusinessEntity getEntity() {
        return entity;
    }

    public void setEntity(BusinessEntity entity) {
        this.entity = entity;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
