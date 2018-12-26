package org.meveo.model.order;

import java.math.BigDecimal;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.quote.Quote;
import org.meveo.model.shared.Address;

/**
 * Order to subscribe to services or [purchase] products or change or cancel existing subscription
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "ORDER")
@Table(name = "ord_order", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ord_order_seq"), })
public class Order extends BusinessCFEntity implements IBillableEntity {

    private static final long serialVersionUID = -9060067698650286828L;

    public static Integer DEFAULT_PRIORITY = 2;

    /**
     * External identifier
     */
    @Column(name = "external_id", length = 100)
    @Size(max = 100)
    private String externalId;

    /**
     * Order number
     */
    @Column(name = "order_number", length = 255)
    @Size(max = 255)
    private String orderNumber;

    /**
     * Delivery instructions
     */
    @Column(name = "delivery_instructions", columnDefinition = "TEXT")
    private String deliveryInstructions;

    /**
     * Date when order was placed
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date", nullable = false, updatable = false)
    @NotNull
    private Date orderDate = new Date();

    /**
     * Order processing start date as requested by a customer
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "req_start_date")
    private Date requestedStartDate;

    /**
     * Order processing start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Expected completion date as requested by a customer
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "req_completion_date")
    private Date requestedCompletionDate;

    /**
     * Expected completion date as set by provider
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exp_completion_date")
    private Date expectedCompletionDate;

    /**
     * Date when order was fully completed
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completion_date")
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
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private OrderStatusEnum status = OrderStatusEnum.IN_CREATION;

    /**
     * Status message
     */
    @Column(name = "status_message", length = 2000)
    private String statusMessage;

    /**
     * A list of order items. Not modifiable once started processing.
     */
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    /**
     * User group that order processing is routed to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routed_to_user_group_id")
    private UserHierarchyLevel routedToUserGroup;

    /**
     * Application/source that order was received from
     */
    @Column(name = "received_from", length = 50)
    private String receivedFromApp;

    /**
     * Invoices produced
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "orders")
    private List<Invoice> invoices = new ArrayList<>();

    /**
     * Expression to calculate Invoice due date delay value
     */
    @Column(name = "due_date_delay_el", length = 2000)
    @Size(max = 2000)
    private String dueDateDelayEL;

    /**
     * Expression to calculate Invoice due date delay value - for Spark
     */
    @Column(name = "due_date_delay_el_sp", length = 2000)
    @Size(max = 2000)
    private String dueDateDelayELSpark;

    /**
     * Allowed payment methods
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    /**
     * Quote that was transformed into order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    /**
     * Billing cycle when invoicing by order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_cycle")
    private BillingCycle billingCycle;

    /**
     * Last billing run that processed this order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run")
    private BillingRun billingRun;

    /**
     * Rated transactions to reach minimum amount per invoice
     */
    @Transient
    private List<RatedTransaction> minRatedTransactions;

    /**
     * Total invoicing amount without tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithoutTax;

    /**
     * Total invoicing amount with tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithTax;

    /**
     * Total invoicing tax amount
     */
    @Transient
    private BigDecimal totalInvoicingAmountTax;

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

    /**
     * @return the invoices
     */
    public List<Invoice> getInvoices() {
        return invoices;
    }

    /**
     * @param invoices the invoices to set
     */
    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public Set<UserAccount> getUserAccounts() {

        Set<UserAccount> userAccounts = new HashSet<>();
        for (OrderItem orderItem : orderItems) {
            userAccounts.add(orderItem.getUserAccount());
        }
        return userAccounts;
    }

    /**
     * @return Order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * @param orderNumber Order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Address getShippingAddress() {
        if (getOrderItems() != null && !getOrderItems().isEmpty()) {
            return getOrderItems().get(0).getShippingAddress();
        }
        return null;
    }

    /**
     * @return Expression to calculate Invoice due date delay value
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * @param dueDateDelayEL Expression to calculate Invoice due date delay value
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    /**
     * @return Expression to calculate Invoice due date delay value - for Spark
     */
    public String getDueDateDelayELSpark() {
        return dueDateDelayELSpark;
    }

    /**
     * @param dueDateDelayELSpark Expression to calculate Invoice due date delay value - for Spark
     */
    public void setDueDateDelayELSpark(String dueDateDelayELSpark) {
        this.dueDateDelayELSpark = dueDateDelayELSpark;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public void setMinRatedTransactions(List<RatedTransaction> ratedTransactions) {
        minRatedTransactions = ratedTransactions;
    }

    public List<RatedTransaction> getMinRatedTransactions() {
        return minRatedTransactions;
    }

    public BigDecimal getTotalInvoicingAmountWithoutTax() {
        return totalInvoicingAmountWithoutTax;
    }

    public void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax) {
        this.totalInvoicingAmountWithoutTax = totalInvoicingAmountWithoutTax;
    }

    public BigDecimal getTotalInvoicingAmountWithTax() {
        return totalInvoicingAmountWithTax;
    }

    public void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax) {
        this.totalInvoicingAmountWithTax = totalInvoicingAmountWithTax;
    }

    public BigDecimal getTotalInvoicingAmountTax() {
        return totalInvoicingAmountTax;
    }

    public void setTotalInvoicingAmountTax(BigDecimal totalInvoicingAmountTax) {
        this.totalInvoicingAmountTax = totalInvoicingAmountTax;
    }
}