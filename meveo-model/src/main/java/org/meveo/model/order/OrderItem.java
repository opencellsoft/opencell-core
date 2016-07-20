package org.meveo.model.order;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ProductOffering;

@Entity
@ExportIdentifier({ "order.code", "itemId", "provider" })
@Table(name = "ORD_ORDER_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ORD_ORDER_ITEM_SEQ")
public class OrderItem extends BaseEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, updatable = false)
    @NotNull
    private Order order;

    @Column(name = "ITEM_ID", length = 10, nullable = false)
    @NotNull
    private String itemId;

    /**
     * Action requested on a product or product offer
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION", length = 10, nullable = false)
    @NotNull
    private OrderItemActionEnum action;

    /**
     * Associated user account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID", nullable = false)
    @NotNull
    private UserAccount userAccount;

    /**
     * Product offerings associated to an order item. In case of bundled offers, the first item in a list is the parent offering.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ORD_ITEM_OFFERINGS", joinColumns = @JoinColumn(name = "ORDER_ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "PRD_OFFERING_ID"))
    private List<ProductOffering> productOfferings = new ArrayList<>();

    /**
     * Serialized orderItem dto.
     */
    @Column(name = "SOURCE", nullable = false, columnDefinition = "TEXT")
    private String source;

    /**
     * Order item processing status as defined by the workflow.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private OrderStatusEnum status;

    /**
     * Related product instances. Product instance(s) are created or updated by workflow while processing order item.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ORD_ITEM_PRD_INSTANCE", joinColumns = @JoinColumn(name = "ORDER_ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "PRD_INSTANCE_ID"))
    private List<ProductInstance> productInstances;

    /**
     * Related subscription. Subscription is created or updated by workflow while processing order item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

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

    public List<ProductOffering> getProductOfferings() {
        return productOfferings;
    }

    public void setProductOfferings(List<ProductOffering> productOfferings) {
        this.productOfferings = productOfferings;
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

    public void addProductInstance(ProductInstance productInstance) {
        if (this.productInstances == null) {
            this.productInstances = new ArrayList<>();
        }
        this.productInstances.add(productInstance);
    }
}