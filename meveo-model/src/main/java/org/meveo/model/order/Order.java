package org.meveo.model.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.hierarchy.UserHierarchyLevel;

@Entity
@ExportIdentifier({ "code", "provider" })
@CustomFieldEntity(cftCodePrefix = "ORDER")
@Table(name = "ORD_ORDER", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ORD_ORDER_SEQ")
public class Order extends BusinessCFEntity {

    private static final long serialVersionUID = -9060067698650286828L;

    public static Integer DEFAULT_PRIORITY = 2;

    /**
     * External identifier
     */
    @Column(name = "EXTERNAL_ID", length = 100)
    @Size(max = 100)
    private String externalId;

    /**
     * Delivery instructions
     */
    @Column(name = "DELIVERY_INSTRUCTIONS", columnDefinition = "TEXT")
    private String deliveryInstructions;

    /**
     * Date when order was placed
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ORDER_DATE", nullable = false, updatable = false)
    @NotNull
    private Date orderDate = new Date();

    /**
     * Order processing start date as requested by a customer
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQ_START_DATE")
    private Date requestedStartDate;

    /**
     * Order processing start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;

    /**
     * Expected completion date as requested by a customer
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQ_COMPLETION_DATE")
    private Date requestedCompletionDate;

    /**
     * Expected completion date as set by provider
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXP_COMPLETION_DATE")
    private Date expectedCompletionDate;

    /**
     * Date when order was fully completed
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "COMPLETION_DATE")
    private Date completionDate;

    /**
     * Completion priority
     */
    @Column(name = "priority")
    private Integer priority = DEFAULT_PRIORITY;

    /**
     * Category
     */
    @Column(name = "category", length = 200)
    private String category;

    /**
     * Order processing status as defined by the workflow.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    @NotNull
    private OrderStatusEnum status = OrderStatusEnum.IN_CREATION;

    @Column(name = "STATUS_MESSAGE", length = 2000)
    private String statusMessage;

    /**
     * A list of order items. Not modifiable once started processing.
     */
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUTED_TO_USER_GROUP_ID")
    private UserHierarchyLevel routedToUserGroup;

    @Column(name = "RECEIVED_FROM", length = 50)
    private String receivedFromApp;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getRequestedStartDate() {
        return requestedStartDate;
    }

    public void setRequestedStartDate(Date requestedStartDate) {
        this.requestedStartDate = requestedStartDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getRequestedCompletionDate() {
        return requestedCompletionDate;
    }

    public void setRequestedCompletionDate(Date requestedCompletionDate) {
        this.requestedCompletionDate = requestedCompletionDate;
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem orderItem) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
    }

    public UserHierarchyLevel getRoutedToUserGroup() {
        return routedToUserGroup;
    }

    public void setRoutedToUserGroup(UserHierarchyLevel routedToUserGroup) {
        this.routedToUserGroup = routedToUserGroup;
    }

    public String getReceivedFromApp() {
        return receivedFromApp;
    }

    public void setReceivedFromApp(String receivedFromApp) {
        this.receivedFromApp = receivedFromApp;
    }

    public Set<UserAccount> getUserAccounts() {

        Set<UserAccount> userAccounts = new HashSet<>();
        for (OrderItem orderItem : orderItems) {
            userAccounts.add(orderItem.getUserAccount());
        }
        return userAccounts;
    }
}