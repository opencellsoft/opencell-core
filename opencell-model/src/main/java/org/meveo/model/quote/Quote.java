package org.meveo.model.quote;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.Order;

/**
 * Quote to subscribe to services or [purchase] products
 * 
 * @author Andrius Karpavicius
 */
@Entity
@WorkflowedEntity
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "QUOTE")
@Table(name = "ord_quote", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ord_quote_seq"), })
public class Quote extends BusinessCFEntity implements IWFEntity {

    private static final long serialVersionUID = -9060067698650286828L;

    public static Integer DEFAULT_PRIORITY = 2;

    /**
     * External identifier
     */
    @Column(name = "external_id", length = 100)
    @Size(max = 100)
    private String externalId;

    /**
     * Quote version because if the customer rejected the quote but negotiations still open a new version of the quote is managed
     */
    @Column(name = "quote_version", length = 10)
    @Size(max = 10)
    private String quoteVersion;

    /**
     * Contact attached to the quote to send back information regarding this quote
     */
    @Column(name = "contact", length = 100)
    @Size(max = 100)
    private String notificationContact;

    /**
     * Date when quote was created
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "quote_date", nullable = false, updatable = false)
    @NotNull
    private Date quoteDate = new Date();

    /**
     * Quote validity dates
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity = new DatePeriod();

    /**
     * Initial quote required by date from the requestor perspective
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "req_completion_date")
    private Date requestedCompletionDate;

    /**
     * Date when product in the quote should be available
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_start_date")
    private Date fulfillmentStartDate;

    /**
     * Date when the quoted was Cancelled or Rejected or Accepted
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completion_date")
    private Date completionDate;

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
    private QuoteStatusEnum status = QuoteStatusEnum.IN_PROGRESS;

    /**
     * Status message
     */
    @Column(name = "status_message", length = 2000)
    private String statusMessage;

    /**
     * A list of qupte items. Not modifiable once send to customer for approval.
     */
    @OneToMany(mappedBy = "quote", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteItem> quoteItems;

    /**
     * User group that quote processing is routed to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routed_to_user_group_id")
    private UserHierarchyLevel routedToUserGroup;

    /**
     * Application/source that quote requets was received from
     */
    @Column(name = "received_from", length = 50)
    private String receivedFromApp;

    /**
     * Associated user account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Associated invoices
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<Invoice>();

    /**
     * Order produced from this quote
     */
    @OneToOne(mappedBy = "quote", fetch = FetchType.LAZY)
    private Order order;

    /**
     * Should PDF be generated
     */
    @Transient
    private boolean generatePdf = true;;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getQuoteVersion() {
        return quoteVersion;
    }

    public void setQuoteVersion(String quoteVersion) {
        this.quoteVersion = quoteVersion;
    }

    public String getNotificationContact() {
        return notificationContact;
    }

    public void setNotificationContact(String notificationContact) {
        this.notificationContact = notificationContact;
    }

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }

    public DatePeriod getValidity() {
        if (validity == null) {
            validity = new DatePeriod();
        }
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    public Date getRequestedCompletionDate() {
        return requestedCompletionDate;
    }

    public void setRequestedCompletionDate(Date requestedCompletionDate) {
        this.requestedCompletionDate = requestedCompletionDate;
    }

    public Date getFulfillmentStartDate() {
        return fulfillmentStartDate;
    }

    public void setFulfillmentStartDate(Date fulfillmentStartDate) {
        this.fulfillmentStartDate = fulfillmentStartDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QuoteStatusEnum getStatus() {
        return status;
    }

    public void setStatus(QuoteStatusEnum status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<QuoteItem> getQuoteItems() {
        return quoteItems;
    }

    public void setQuoteItems(List<QuoteItem> quoteItems) {
        this.quoteItems = quoteItems;
    }

    public void addQuoteItem(QuoteItem quoteItem) {
        if (this.quoteItems == null) {
            this.quoteItems = new ArrayList<>();
        }
        this.quoteItems.add(quoteItem);
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

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Set<UserAccount> getUserAccounts() {

        Set<UserAccount> userAccounts = new HashSet<>();
        if (userAccount != null) {
            userAccounts.add(userAccount);
        }
        for (QuoteItem quoteItem : quoteItems) {
            userAccounts.add(quoteItem.getUserAccount());
        }
        return userAccounts;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isGeneratePdf() {
        return generatePdf;
    }

    public void setGeneratePdf(boolean generatePdf) {
        this.generatePdf = generatePdf;
    }
}