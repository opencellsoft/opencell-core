package org.meveo.model.quote;

import static javax.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.ProductActionTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;

@SuppressWarnings("serial")
@Entity
@CustomFieldEntity(cftCodePrefix = "QuoteProduct")
@Table(name = "cpq_quote_product")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_product_seq"), })
@NamedQueries({
		@NamedQuery(name = "QuoteProduct.findByQuoteId", query = "select q from QuoteProduct q where q.quote.id=:id"),
		@NamedQuery(name = "QuoteProduct.findByQuoteVersionId", query = "select q from QuoteProduct q where q.quoteVersion.id=:id"),
		@NamedQuery(name = "QuoteProduct.findByQuoteVersionAndQuoteOffer", query = "select q from QuoteProduct q left join q.quoteVersion qq left join q.quoteOffer qqo left join q.productVersion pv where qq.id=:quoteVersionId and qqo.code=:quoteOfferCode and pv.product.code=:productCode"),
		@NamedQuery(name = "QuoteProduct.findQuoteAttribute", query = "select qp from QuoteProduct qp left join qp.quoteVersion qv left join qp.quoteOffer qf left join qp.productVersion pv "
				+ " where qv.id=:quoteVersionId and qf.offerTemplate.code=:offerCode and pv.product.code=:productCode ")

})
public class QuoteProduct extends AuditableCFEntity {

	/**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_id", referencedColumnName = "id")
    private CpqQuote quote;

    /**
     * quote Version
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_version_id", referencedColumnName = "id")
    private QuoteVersion quoteVersion;

    /**
     * product
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id", referencedColumnName = "id")
	@NotNull
    private ProductVersion productVersion;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_quote_id", referencedColumnName = "id")
    private QuoteOffer quoteOffer;
    

    @OneToMany(mappedBy = "quoteProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteAttribute> quoteAttributes = new ArrayList<>();
    

    @OneToMany(mappedBy = "quoteProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<QuoteArticleLine> quoteArticleLines = new ArrayList<>();
    
	/**
	 * discountPlan attached to this quoteProduct
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    /** Delivery timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivery_date")
    private Date deliveryDate;
    
    /**production action type */
    @Enumerated(EnumType.STRING)
    @Column(name = "product_action_type", length = 10)
   	private ProductActionTypeEnum productActionType;
    
    /** termination timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;
    
    /** Termination reason. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason terminationReason;

	public QuoteProduct() {
	}

	public QuoteProduct(QuoteProduct copy) {
		this.quote = copy.quote;
		this.quoteVersion = copy.quoteVersion;
		this.productVersion = copy.productVersion;
		this.quantity = copy.quantity;
		this.discountPlan=copy.getDiscountPlan();
		this.quoteOffer = copy.quoteOffer;
		this.quoteAttributes = copy.quoteAttributes;
		this.deliveryDate = copy.deliveryDate;
		this.productActionType = copy.productActionType;
		this.terminationDate = copy.terminationDate;
		this.terminationReason = copy.terminationReason;
	}
	
	public void update(QuoteProduct other) {
    	this.quoteOffer = other.quoteOffer;
    	this.quote = other.quote;
		this.quoteVersion = other.quoteVersion;
		this.productVersion = other.productVersion;
		this.quantity = other.quantity;
		this.discountPlan=other.getDiscountPlan();
		this.quoteOffer = other.quoteOffer;
		this.quoteAttributes = other.quoteAttributes;
		this.deliveryDate = other.deliveryDate;
		this.productActionType = other.productActionType;
		this.terminationDate = other.terminationDate;
		this.terminationReason = other.terminationReason;
    }

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}
	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	/**
	 * @return the quote
	 */
	public CpqQuote getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(CpqQuote quote) {
		this.quote = quote;
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
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}



	/**
	 * @return the productVersion
	 */
	public ProductVersion getProductVersion() {
		return productVersion;
	}

	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}


	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}


	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(productVersion, quantity, quote, quoteOffer, quoteVersion);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		QuoteProduct other = (QuoteProduct) obj;
		return  Objects.equals(productVersion, other.productVersion) && Objects.equals(quantity, other.quantity)
				&& Objects.equals(quote, other.quote)
				&& Objects.equals(quoteOffer, other.quoteOffer) && Objects.equals(quoteVersion, other.quoteVersion) && Objects.equals(discountPlan, other.discountPlan);
	}
	/**
	 * @return the quoteAttributes
	 */
	public List<QuoteAttribute> getQuoteAttributes() {
		if(quoteAttributes == null)
			quoteAttributes = new ArrayList<>();
		return quoteAttributes;
	}
	/**
	 * @param quoteAttributes the quoteAttributes to set
	 */
	public void setQuoteAttributes(List<QuoteAttribute> quoteAttributes) {
		this.quoteAttributes = quoteAttributes;
	}


	/**
	 * @return the quoteArticleLines
	 */
	public List<QuoteArticleLine> getQuoteArticleLines() {
		return quoteArticleLines;
	}


	/**
	 * @param quoteArticleLines the quoteArticleLines to set
	 */
	public void setQuoteArticleLines(List<QuoteArticleLine> quoteArticleLines) {
		this.quoteArticleLines = quoteArticleLines;
	}

	/**
	 * 
	 * @return delivery date
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * 
	 * @param deliveryDate
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	public ProductActionTypeEnum getProductActionType() {
		return productActionType;
	}


	public void setProductActionType(ProductActionTypeEnum productActionType) {
		this.productActionType = productActionType;
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
}
