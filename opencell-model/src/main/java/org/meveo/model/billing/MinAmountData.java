/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Store needed data used to create a new Rated transaction from minimum amount.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
public class MinAmountData {
    /**
     * The minimum amount.
     */
    private BigDecimal minAmount;
    /**
     * The minimum amounts label.
     */
    private String minAmountLabel;
    /**
     * The amounts object.
     */
    private Amounts amounts;
    /**
     * Min amount by invoiceSubCateory.
     */
    private Map<Long, Amounts> invoiceSubCategoryAmounts;

    /**
     * The entity having the minimum amount, customer, CA, BA, UA, subscription or serviceInstance.
     */
    private BusinessEntity entity;

    /**
     * The seller.
     */
    private Seller seller;

    /**
     * The constructor.
     *
     * @param minAmount                 The minimum amount
     * @param minAmountLabel            The minimum amount label
     * @param amounts                   The Amounts
     * @param invoiceSubCategoryAmounts The amounts grouped by invoiceSubCategory
     * @param entity                    the entity
     * @param seller                    the seller
     */
    public MinAmountData(BigDecimal minAmount, String minAmountLabel, Amounts amounts, Map<Long, Amounts> invoiceSubCategoryAmounts, BusinessEntity entity, Seller seller) {
        this.minAmount = minAmount;
        this.minAmountLabel = minAmountLabel;
        this.amounts = amounts;
        this.invoiceSubCategoryAmounts = invoiceSubCategoryAmounts;
        this.entity = entity;
        this.seller = seller;
    }

    /**
     * Gets the minimum amount.
     *
     * @return the minimum amount as a BigDecimal
     */
    public BigDecimal getMinAmount() {
        return minAmount;
    }

    /**
     * Sets the minimum amount.
     *
     * @param minAmount the minimum amount
     */
    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * Gets the minimum amount label.
     *
     * @return The minimum amount label
     */
    public String getMinAmountLabel() {
        return minAmountLabel;
    }

    /**
     * Sets the minimum amount label.
     *
     * @param minAmountLabel The minimum amount label
     */
    public void setMinAmountLabel(String minAmountLabel) {
        this.minAmountLabel = minAmountLabel;
    }

    /**
     * Gets the Amounts.
     *
     * @return the Amounts.
     */
    public Amounts getAmounts() {
        return amounts;
    }

    /**
     * Sets the Amounts.
     *
     * @param amounts the Amounts
     */
    public void setAmounts(Amounts amounts) {
        this.amounts = amounts;
    }

    /**
     * Gets Amounts map grouped by InvoiceSubCategory.
     *
     * @return A map of Amounts grouped by InvoiceSubCategory
     */
    public Map<Long, Amounts> getInvoiceSubCategoryAmounts() {
        return invoiceSubCategoryAmounts;
    }

    /**
     * Sets Amounts map.
     *
     * @param invoiceSubCategoryAmounts A map of Amounts grouped by InvoiceSubCategory
     */
    public void setInvoiceSubCategoryAmounts(Map<Long, Amounts> invoiceSubCategoryAmounts) {
        this.invoiceSubCategoryAmounts = invoiceSubCategoryAmounts;
    }

    /**
     * Gets the entity.
     *
     * @return a BusinessEntity
     */
    public BusinessEntity getEntity() {
        return entity;
    }

    /**
     * Sets the entity.
     *
     * @param entity a BusinessEntity
     */
    public void setEntity(BusinessEntity entity) {
        this.entity = entity;
    }

    /**
     * Gets a seller.
     *
     * @return a seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller a Seller
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
