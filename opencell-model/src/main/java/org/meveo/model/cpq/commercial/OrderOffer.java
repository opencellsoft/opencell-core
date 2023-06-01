package org.meveo.model.cpq.commercial;

import static javax.persistence.FetchType.LAZY;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.offer.QuoteOffer;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@WorkflowedEntity
@CustomFieldEntity(cftCodePrefix = "OrderOffer",inheritCFValuesFrom = "quoteOffer")
@Table(name = "cpq_order_offer", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "order_id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_offer_seq")})
@NamedQueries({
		@NamedQuery(name = "OrderOffer.findByCodeAndOrderCode", query = "select oo from OrderOffer oo left join oo.order oorder  where oorder.code=:orderCode  and oo.code=:code"),
		@NamedQuery(name = "OrderOffer.findByStatusAndSubscription", query = "select oo from OrderOffer oo join oo.order oorder  where oo.subscription.code=:subscriptionCode and oo.orderLineType=:status and oorder.status<>'VALIDATED'")
})
public class OrderOffer extends BusinessCFEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362016936635761040L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", nullable = false)
	@NotNull
	private OfferTemplate offerTemplate;

	@OneToMany(mappedBy = "orderOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderProduct> products = new HashSet<>();

	@OneToMany(mappedBy = "orderOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<OrderAttribute> orderAttributes = new ArrayList<>();
	
	/**
	 * discountPlan attached to this orderOffer
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    /**
	 * quote offer attached to this orderOffer
	 */
    
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_offer_id")
	private QuoteOffer quoteOffer;
	
	 /** Delivery timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivery_date")
    private Date deliveryDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_line_type", length = 20)
    private OfferLineTypeEnum orderLineType = OfferLineTypeEnum.CREATE;
    
    /**
     * Subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
    
    /** termination timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;
    
    /** Termination reason. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason terminationReason;
    
    /** FrArgs contract. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;
    
	
    @Override
	public ICustomFieldEntity[] getParentCFEntities() {
		if (quoteOffer != null) {
			return new ICustomFieldEntity[] { quoteOffer };
		}
		return null;
	}
    
	/**
	 * @return the order
	 */
	public CommercialOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(CommercialOrder order) {
		this.order = order;
	}

	/**
	 * @return the offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	/**
	 * @param offerTemplate the offerTemplate to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public List<OrderProduct> getProducts() {
		return new ArrayList<>(this.products);
	}

	public void setProducts(List<OrderProduct> products) {
		this.products = new HashSet<>(products);
	}

	/**
	 * @return the orderAttributes
	 */
	public List<OrderAttribute> getOrderAttributes() {
		return orderAttributes;
	}

	/**
	 * @param orderAttributes the orderAttributes to set
	 */
	public void setOrderAttributes(List<OrderAttribute> orderAttributes) {
		this.orderAttributes = orderAttributes;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}

	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the userAccount
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}

	/**
	 * @param userAccount the userAccount to set
	 */
	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

    /**
     * @return the orderLineType
     */
    public OfferLineTypeEnum getOrderLineType() {
        return orderLineType;
    }

    /**
     * @param orderLineType the orderLineType to set
     */
    public void setOrderLineType(OfferLineTypeEnum orderLineType) {
        this.orderLineType = orderLineType;
    }

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public SubscriptionTerminationReason getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
		this.terminationReason = terminationReason;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
}
