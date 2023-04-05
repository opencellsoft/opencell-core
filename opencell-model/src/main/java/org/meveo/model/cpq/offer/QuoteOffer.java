package org.meveo.model.cpq.offer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;


@Entity
@CustomFieldEntity(cftCodePrefix = "QuoteOffer")
@Table(name="quote_offer", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "quote_version_id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "quote_offer_seq"), })
@NamedQueries({
		@NamedQuery(name = "QuoteOffer.findByCodeAndQuoteVersion", query = "select qf from QuoteOffer qf left join qf.quoteVersion qv  where qv.id=:quoteVersionId and qf.code=:code"),
		@NamedQuery(name = "QuoteOffer.findByStatusAndSubscription", query = "select oo from QuoteOffer oo left join oo.quoteVersion qv where oo.subscription.code=:subscriptionCode and oo.quoteLineType=:status")
})
public class QuoteOffer extends BusinessCFEntity {


	public QuoteOffer() {}


	public QuoteOffer(QuoteOffer copy) {
		this.offerTemplate = copy.offerTemplate;
		this.billableAccount = copy.billableAccount;
		this.quoteVersion = copy.quoteVersion;
		this.quoteLot = copy.quoteLot;
		this.quoteProduct = copy.quoteProduct;
		this.contract = copy.contract;
		this.position = copy.position;
		this.cfValues = copy.getCfValues();
		this.sequence = copy.sequence;
		this.deliveryDate = copy.deliveryDate;
		this.userAccount = copy.userAccount;
		this.quoteLineType = copy.quoteLineType;
		this.subscription = copy.subscription;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8394446810964282119L;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", nullable = false)
	@NotNull
	private OfferTemplate offerTemplate;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billable_account_id")
	private BillingAccount billableAccount;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_version_id", nullable = false)
	@NotNull
	private QuoteVersion quoteVersion;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_customer_service_id")
	private QuoteLot quoteLot;
	

    @OneToMany(mappedBy = "quoteOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteProduct> quoteProduct;
    
    
    @OneToMany(mappedBy = "quoteOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteAttribute> quoteAttributes = new ArrayList<QuoteAttribute>();
	
	/**
	 * opportunityRef
	 */
	@Column(name = "position")
	private Integer position;

	/**
	 * discountPlan attached to this quoteOffer
	 */
    @ManyToOne
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    @Column(name = "sequence")
    protected Integer sequence=0;
    
    
    @OneToMany(mappedBy = "quoteOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuotePrice> quotePrices = new ArrayList<QuotePrice>();
    
    /** Delivery timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivery_date")
    private Date deliveryDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id") 
    private UserAccount userAccount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "quote_line_type", length = 10)
    private OfferLineTypeEnum quoteLineType = OfferLineTypeEnum.CREATE;
    
    /**
     * Subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}
	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
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

	/**
	 * @return the billableAccount
	 */
	public BillingAccount getBillableAccount() {
		return billableAccount;
	}

	/**
	 * @param billableAccount the billableAccount to set
	 */
	public void setBillableAccount(BillingAccount billableAccount) {
		this.billableAccount = billableAccount;
	}

	/**
	 * @return the quoteVersion
	 */
	public QuoteVersion getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(QuoteVersion quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


	/**
	 * @return the quoteProduct
	 */
	public List<QuoteProduct> getQuoteProduct() {
		if(quoteProduct == null)
			quoteProduct = new ArrayList<>();
		return quoteProduct;
	}

	/**
	 * @param quoteProduct the quoteProduct to set
	 */
	public void setQuoteProduct(List<QuoteProduct> quoteProduct) {
		this.quoteProduct = quoteProduct;
	}



	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the quoteLot
	 */
	public QuoteLot getQuoteLot() {
		return quoteLot;
	}

	/**
	 * @param quoteLot the quoteLot to set
	 */
	public void setQuoteLot(QuoteLot quoteLot) {
		this.quoteLot = quoteLot;
	}

	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}


	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	

	/**
	 * @return the quoteAttributes
	 */
	public List<QuoteAttribute> getQuoteAttributes() {
		return quoteAttributes;
	}


	/**
	 * @param quoteAttributes the quoteAttributes to set
	 */
	public void setQuoteAttributes(List<QuoteAttribute> quoteAttributes) {
		this.quoteAttributes = quoteAttributes;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(billableAccount, offerTemplate, quoteLot, quoteProduct, quoteVersion);
		return result;
	}
	
	

	public List<QuotePrice> getQuotePrices() {
		return quotePrices;
	}


	public void setQuotePrices(List<QuotePrice> quotePrices) {
		this.quotePrices = quotePrices;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		QuoteOffer other = (QuoteOffer) obj;
		return Objects.equals(id, other.id);
	}


	public Date getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	public UserAccount getUserAccount() {
		return userAccount;
	}


	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}


    /**
     * @return the quoteLineType
     */
    public OfferLineTypeEnum getQuoteLineType() {
        return quoteLineType;
    }


    /**
     * @param quoteLineType the quoteLineType to set
     */
    public void setQuoteLineType(OfferLineTypeEnum quoteLineType) {
        this.quoteLineType = quoteLineType;
    }


	public Subscription getSubscription() {
		return subscription;
	}


	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
	
	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}
	/**
	 * @param contract the contract to set
	 */
	public void setContract(Contract contract) {
		this.contract = contract;
	}
}
