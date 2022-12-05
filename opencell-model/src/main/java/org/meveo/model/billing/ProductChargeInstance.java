/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;

import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.catalog.ProductChargeTemplate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Product charge as part of purchased product. Can be either part of subscription or not.
 * 
 * @author Andrius Karpavicius
 */
@Entity
@DiscriminatorValue("P")
public class ProductChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    /**
     * Charge template/definition that charge was instantiated from
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id", insertable = false, updatable = false)
    private ProductChargeTemplate productChargeTemplate;

    /**
     * Purchased product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_instance_id")
    private ProductInstance productInstance;

    /**
     * Quantity purchased
     */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal quantity = BigDecimal.ONE;

    public ProductChargeInstance(ProductInstance productInstance, ProductChargeTemplate productChargeTemplate) {
        this.code = productInstance.getCode();
        this.description = productInstance.getDescription();
        this.chargeDate = productInstance.getApplicationDate();
        this.userAccount = productInstance.getUserAccount();
        this.subscription = productInstance.getSubscription();
        this.setSeller(productInstance.getSeller());
        this.setCountry(userAccount.getBillingAccount().getTradingCountry());
        this.setCurrency(userAccount.getBillingAccount().getTradingCurrency());
        this.productInstance = productInstance;
        this.status = InstanceStatusEnum.ACTIVE;
        this.setQuantity(productInstance.getQuantity() == null ? BigDecimal.ONE : productInstance.getQuantity());
        this.chargeTemplate = productChargeTemplate;
        this.productChargeTemplate = productChargeTemplate;
    }

    public ProductChargeInstance() {

    }

    public ProductChargeTemplate getProductChargeTemplate() {
        return productChargeTemplate;
    }

    public void setProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
        this.productChargeTemplate = productChargeTemplate;
    }

    public ProductInstance getProductInstance() {
        return productInstance;
    }

    public void setProductInstance(ProductInstance productInstance) {
        this.productInstance = productInstance;
    }

    /**
     * @return Quantity purchased
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * @param quantity Quantity purchased
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public ChargeMainTypeEnum getChargeMainType() {
        return ChargeMainTypeEnum.PRODUCT;
    }
}