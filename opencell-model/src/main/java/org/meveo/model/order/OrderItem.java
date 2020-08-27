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

package org.meveo.model.order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.shared.Address;

/**
 * Order item
 * 
 * @author Andrius Karpavicius
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "OrderItem")
@WorkflowedEntity
@ExportIdentifier({ "order.code", "itemId" })
@Table(name = "ord_order_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ord_order_item_seq"), })
public class OrderItem extends BusinessCFEntity implements IWFEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    /**
     * Order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    @NotNull
    private Order order;

    /**
     * Item id in the order
     */
    @Column(name = "item_id", length = 10, nullable = false)
    @NotNull
    private String itemId;

    /**
     * Action requested on a product or product offer
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 10, nullable = false)
    @NotNull
    private OrderItemActionEnum action;

    /**
     * Associated user account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Product offerings associated to an order item. In case of bundled offers, the first item in a list is the parent offering.
     */
    @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ITEM_ORDER")
    private List<OrderItemProductOffering> orderItemProductOfferings = new ArrayList<>();

    /**
     * Serialized orderItem dto
     */
    @Column(name = "source", nullable = false, columnDefinition = "TEXT")
    private String source;

    /**
     * Order item processing status as defined by the workflow.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private OrderStatusEnum status = OrderStatusEnum.IN_CREATION;

    /**
     * Related product instances. Product instance(s) are created or updated by workflow while processing order item.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ord_item_prd_instance", joinColumns = @JoinColumn(name = "order_item_id"), inverseJoinColumns = @JoinColumn(name = "prd_instance_id"))
    private List<ProductInstance> productInstances;

    /**
     * Related subscription. Subscription is created or updated by workflow while processing order item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Shipping address
     */
    @Embedded
    private Address shippingAddress = new Address();

    /**
     * Order action history
     */
    @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHistory> orderHistories = new ArrayList<>();

    /**
     * Source of order in XML format
     */
    @Transient
    private Object orderItemDto;
    

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;
    
    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;
    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Main product offering
     */
    @Transient
    private ProductOffering mainOffering;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public OrderItemActionEnum getAction() {
        return action;
    }

    public void setAction(OrderItemActionEnum action) {
        this.action = action;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String orderItemSource) {
        this.source = orderItemSource;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public List<ProductInstance> getProductInstances() {
        return productInstances;
    }

    public void setProductInstances(List<ProductInstance> productInstances) {
        this.productInstances = productInstances;
    }

    /**
     * @return the shippingAddress
     */
    public Address getShippingAddress() {
        return shippingAddress;
    }

    /**
     * @param shippingAddress the shippingAddress to set
     */
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void addProductInstance(ProductInstance productInstance) {
        if (this.productInstances == null) {
            this.productInstances = new ArrayList<>();
        }
        this.productInstances.add(productInstance);
    }

    // public ProductInstance getProductInstance() {
    // Logger log = LoggerFactory.getLogger(getClass());
    // if (productInstances != null && productInstances.size() > 0) {
    // return productInstances.get(0);
    // }
    // return null;
    // }

    public Object getOrderItemDto() {
        return orderItemDto;
    }

    public void setOrderItemDto(Object orderItemDto) {
        this.orderItemDto = orderItemDto;
    }

    public ProductOffering getMainOffering() {

        if (mainOffering == null && orderItemProductOfferings != null && !orderItemProductOfferings.isEmpty()) {
            mainOffering = orderItemProductOfferings.get(0).getProductOffering();
        }

        return mainOffering;
    }

    public void setMainOffering(ProductOffering mainOffering) {
        this.mainOffering = mainOffering;
    }

    public void resetMainOffering(ProductOffering newMainOffer) {
        this.mainOffering = newMainOffer;
        orderItemProductOfferings.clear();
        if (newMainOffer != null) {
            orderItemProductOfferings.add(new OrderItemProductOffering(this, newMainOffer, orderItemProductOfferings.size()));
        }
        orderItemDto = null;
        source = null;
    }

    public List<OrderItemProductOffering> getOrderItemProductOfferings() {
        return orderItemProductOfferings;
    }

    public void setOrderItemProductOfferings(List<OrderItemProductOffering> orderItemProductOfferings) {
        this.orderItemProductOfferings = orderItemProductOfferings;
    }

    /**
     * Interested in comparing order items within the order only
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OrderItem)) {
            return false;
        }

        OrderItem other = (OrderItem) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        return StringUtils.compare(getItemId(), other.getItemId()) == 0;
    }

    public List<OrderHistory> getOrderHistories() {
        return orderHistories;
    }

    public void setOrderHistories(List<OrderHistory> orderHistories) {
        this.orderHistories = orderHistories;
    }
    
    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }
    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }
    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }
    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }
    @Override
    public String getUuid() {
        setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }
    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }
}