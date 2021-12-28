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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.ProductTemplate;

/**
 * Purchased product
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "ProductInstance", inheritCFValuesFrom = "productTemplate")
@ExportIdentifier({ "code" })
@Table(name = "billing_product_instance")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_product_instance_seq"), })
public class ProductInstance extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Associated User account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Subscription. Optional. Null if purchase is not part of subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Product template/definition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_template_id")
    private ProductTemplate productTemplate;

    /**
     * Purchase date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "application_date")
    private Date applicationDate = new Date();

    /**
     * Instantiated product charges
     */
    @OneToMany(mappedBy = "productInstance", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductChargeInstance> productChargeInstances = new ArrayList<>();

    /**
     * Quantity purchased
     */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal quantity = BigDecimal.ONE;

    /**
     * Order number when purchase was initiated by an order
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Seller that offered product to purchase
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    /**
     * Instantiate a product instance
     */
    public ProductInstance() {
        super();
    }

    /**
     * Instantiate a product instance
     * 
     * @param userAccount User account
     * @param subscription Subscription if tied to one
     * @param productTemplate Product template
     * @param quantity Quantity
     * @param applicationDate Application date
     * @param code Code to assign
     * @param description Description
     * @param orderNumber Order number if tied to one
     * @param seller Seller. Defaults to subscription.seller in case product instance is tied to a subscription.
     */
    public ProductInstance(UserAccount userAccount, Subscription subscription, ProductTemplate productTemplate, BigDecimal quantity, Date applicationDate, String code,
            String description, String orderNumber, Seller seller) {
        this.applicationDate = applicationDate;
        this.code = code;
        this.description = description;
        this.productChargeInstances = new ArrayList<>();
        this.productTemplate = productTemplate;
        this.quantity = quantity;
        this.orderNumber = orderNumber;
        this.subscription = subscription;
        if (subscription == null) {
            this.userAccount = userAccount;
            this.seller = seller;
        } else {
            this.userAccount = subscription.getUserAccount();
            this.seller = subscription.getSeller();
        }
        if(this.seller == null) {
            this.seller = this.userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }
    }

    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
    }

    public List<ProductChargeInstance> getProductChargeInstances() {
        return productChargeInstances;
    }

    public void setProductChargeInstances(List<ProductChargeInstance> productChargeInstances) {
        this.productChargeInstances = productChargeInstances;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ProductInstance)) {
            return false;
        }

        ProductInstance other = (ProductInstance) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (productTemplate != null) {
            return new ICustomFieldEntity[] { productTemplate };
        }
        return null;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

}